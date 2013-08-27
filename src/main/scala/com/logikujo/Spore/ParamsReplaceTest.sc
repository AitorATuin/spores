/**
 *
 * spores / LogiDev - [Fun Functional] / Logikujo.com
 *
 * com.logikujo.Spore 25/08/13 :: 20:06 :: eof
 *
 */
import com.logikujo.Spore._
import scalaz._
import Scalaz._
val path = "/examplePath/justOneParam.:param1/aComposedParamWith.:param2.And:param3/:singleParam"




val myMap = Map(
  "param1" -> "P1",
  "param2" -> "P2",
  "param3" -> "P3",
  "singleParam" -> "SP"
)

val replacer = ParamsReplacer(path)



val listOfMaps = (0 to myMap.size) map (myMap.slice(0, _)) toList





val createString = (l: List[String]) => l.fold("") {
  _ ++ "/" ++ _
}
val listOfResults = for {
  m <- listOfMaps
} yield replacer.replace(m)









def showFailure(l: List[String], size: Int) = {
  println(s"Failure in Map with $size args:")
  l.foreach(s => println("\t" ++ s))
}
def showSuccess(s: String, size: Int) = {
  println(s"Success in Map with $size args:")
  println("\t" ++ s)
}
(listOfMaps.map(_.size) zip listOfResults).foreach {
  (r) => r._2 match {
    case Failure(l) => showFailure(l.toList, r._1)
    case Success(s) => showSuccess(s, r._1)
  }
}

























