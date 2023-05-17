package de.bwhc.catalogs.med



import play.api.libs.json.{Json,Format,Writes,Reads,JsString}


final case class Medication
(
  code: Medication.Code,
  name: String,
  version: String,
  kind: Medication.Kind.Value,
  parent: Option[Medication.Code],
  children: Set[Medication.Code]
)


object Medication
{

  case class Code(value: String) extends AnyVal

  object Kind extends Enumeration
  {
    val Group     = Value("group")
    val Substance = Value("substance")

    implicit val format = Json.formatEnum(this)
  }


  implicit val formatCode = Json.valueFormat[Code]

  implicit val format     = Json.format[Medication]

}
