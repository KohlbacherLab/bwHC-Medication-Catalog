package de.bwhc.catalogs.med



import play.api.libs.json.{Json,Format,Writes,Reads,JsString}


final case class Medication
(
  code: Medication.Code,
  name: String,
  version: String,
  parent: Option[Medication.Code],
  children: Set[Medication.Code]
)


object Medication
{

  case class Code(value: String) extends AnyVal

  implicit val formatCode = Json.valueFormat[Code]

  implicit val format     = Json.format[Medication]

}
