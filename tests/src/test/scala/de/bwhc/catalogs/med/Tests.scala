package de.bwhc.catalogs.med


import scala.util.matching.Regex
import scala.io.Source

import org.scalatest.AsyncFlatSpec
import org.scalatest.OptionValues._

import de.bwhc.catalogs.med._


object Setup
{
  val catalogTry = MedicationCatalog.getInstance
}


class Tests extends AsyncFlatSpec
{


  "MedicationCatalog" should "be successfully loaded" in {

    assert(Setup.catalogTry.isSuccess)

  }

  lazy val catalog = Setup.catalogTry.get


  val group = "([A-Z]{1}[0-9]{2}[A-Z]{2})".r


  it should "contain the expected number of groups + substances" in {

    assert(
      catalog.availableVersions
        .forall { v =>

          val expected =
            Source.fromInputStream(
              this.getClass.getClassLoader.getResourceAsStream(s"ATC_$v.csv")
            )
            .getLines()
            .filter(line => group.findPrefixOf(line).isDefined)  
            .size 
          
         catalog.entries(v).size == expected
      }
    )

  }


  it should "return matches for 'umab'" in {

    assert(
      catalog
        .findMatching(
          "umab",
          Some(catalog.latestVersion)
        )
        .nonEmpty)

  }


  it should "contain children for 'Proteinkinase-Inhibitoren'" in {

    val proteinKinaseInhibitors =
      catalog.entries(catalog.latestVersion)
        .find(_.name contains "Proteinkinase-Inhibitoren")
        .value
        .children

    assert(
      proteinKinaseInhibitors.nonEmpty &&
      proteinKinaseInhibitors.forall(catalog.find(_,catalog.latestVersion).isDefined)
    )

  }


}
