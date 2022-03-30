package de.bwhc.catalogs.med



import java.time.Year

import play.api.libs.json.{Json,Format,Writes,Reads,JsString}


final case class Medication
(
  code: Medication.Code,
  name: String,
  version: Year,
  parent: Option[Medication.Code],
  children: Set[Medication.Code]
)


object Medication
{

  case class Code(value: String) extends AnyVal

  implicit val formatCode = Json.valueFormat[Code]


  implicit val formatYear =
    Format[Year](
      Reads(js => js.validate[Int].map(Year.of)),
      Writes(y => JsString(y.toString))
    ) 

  implicit val format     = Json.format[Medication]

}
