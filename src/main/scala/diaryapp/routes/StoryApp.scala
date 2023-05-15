package diaryapp.routes

import diaryapp.persistence.model.{ReplaceProfileData, Story, StoryData}
import diaryapp.persistence.model.StoryData._
import diaryapp.routes.ServerUtils.parseBody
import diaryapp.services.{ProfileIdMissing, StoryService}
import faunadb.query.{Collection, Ref, Time}
import faunadb.values.{Native, RefV, TimeV}
import zhttp.http._
import zio._
import zio.json.EncoderOps

import java.lang.System._
import java.time.Instant
case class StoryApp(storyService: StoryService) {

  def routes(): Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
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


  }
}

object StoryApp {
  val layer:URLayer[StoryService, StoryApp] = ZLayer.fromFunction(StoryApp.apply _)
}
