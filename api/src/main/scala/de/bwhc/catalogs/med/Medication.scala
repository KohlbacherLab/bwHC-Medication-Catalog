package de.bwhc.catalogs.med



import play.api.libs.json.Json



final case class Medication
(
  code: Medication.Code,
  name: Option[String]
)


object Medication
{

  case class Code(value: String) extends AnyVal

  implicit val formatCode = Json.valueFormat[Code]

  implicit val format     = Json.format[Medication]

}
