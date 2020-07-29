package de.bwhc.catalogs.med



object Medication
{
  case class Code(value: String) extends AnyVal
}


final case class Medication
(
  code: Medication.Code,
  name: Option[String]
)
