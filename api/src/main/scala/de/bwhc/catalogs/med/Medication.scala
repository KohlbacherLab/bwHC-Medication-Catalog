package de.bwhc.catalogs.med



object Medication
{
  case class Code(value: String)
}

case class Medication
(
  code: Medication.Code,
  name: String
)
