package diaryapp.routes

import diaryapp.persistence.model.StoryData._
import diaryapp.persistence.model.{Page, Story, StoryData, StoryDataList}
import diaryapp.routes.ServerUtils.parseBody
import diaryapp.services.{JWTAuthenticationService, StoryService}
import faunadb.values.{Native, RefV, TimeV}
import zhttp.http._
import zhttp.http.Middleware.bearerAuth
import zio._
import zio.json.EncoderOps

import java.time.Instant
case class StoryApp(storyService: StoryService, jwtAuthenticationService: JWTAuthenticationService) {

  def routes(): Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "story" =>
    storyService.findAll().map { page =>
      StoryDataList(stories = createListOfStories(page), before = page.before, after = page.after)
    }.map( r => Response.json(r.toJson))
    case Method.GET -> !! / "story" / id  =>
     for {
        story <- storyService.find(id)
      } yield story.map { value =>
       val response = Response.json(Story.toStoryData(value).toJson)
       response.copy(status = Status.Ok)
     }.getOrElse(Response(Status.NotFound))
    case req @ Method.POST -> !! / "story" =>
      for {
        storyData <- parseBody[StoryData](req)
        nextid <- storyService.nextId()
        story = Story(id = nextid,
                     body = storyData.body,
                     tags = storyData.tags.getOrElse(Seq.empty),
                     owner = RefV(storyData.profileId.getOrElse(""), RefV("Profile", Native.Collections)),
          dateCreated = TimeV(Instant.ofEpochMilli(java.lang.System.currentTimeMillis()))
                )
        savedStory <- storyService.save(story)
      } yield {
          val response = Response.json(Story.toStoryData(savedStory).toJson)
          response.copy(status = Status.Created)
      }
  } @@ bearerAuth(jwtAuthenticationService.jwtDecode(_).isDefined)

  def createListOfStories(page: Page[Story]): Seq[StoryData] = page.data.map { story =>
    StoryData(
      id = Some(story.id),
      body = story.body,
      tags = Some(story.tags),
      profileId = Some(story.owner.id)
    )
  }

}

object StoryApp {
  val layer:URLayer[JWTAuthenticationService with StoryService, StoryApp] = ZLayer.fromFunction(StoryApp.apply _)
}
