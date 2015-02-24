/*
 * Copyright 2015 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.hmrc

import org.joda.time.LocalDate
import org.scalatest.{FlatSpec, FunSpec, Matchers, OptionValues}
import uk.gov.hmrc.bobby.Nexus
import uk.gov.hmrc.bobby.domain.Core.DependencyName
import uk.gov.hmrc.bobby.domain.{VersionRange, Exclude, Version}

class NexusPluginSpec extends FlatSpec with Matchers {

  "10.1.8.6.8.5" should "be shortened to 10.1" in {
    Nexus.shortenScalaVersion("10.1.8.6.8.5") shouldBe "10.1"
  }

  "2.10" should "be shortened to 2.10" in {
    Nexus.shortenScalaVersion("2.10") shouldBe "2.10"
  }
}

class CoreSpec extends FunSpec with Matchers with OptionValues{

  it("should read mandatory versions"){

    val mandatoryVersionsString = """
    [
      {
        "organisation" : "uk.gov.hmrc",
        "name" : "play-frontend",
        "excludes" : [
          { "range" : "(,7.4.1)", "reason" : "minimum", "from": "2015-01-01" },
          { "range" : "[8.0.0, 8.4.1]", "reason" : "reason", "from": "2015-03-01" }
        ]
      }
    ]
    """

    val mandatoryVersions: Map[DependencyName, Seq[Exclude]] = uk.gov.hmrc.bobby.domain.Core.getMandatoryVersionsJson(mandatoryVersionsString)

    val parsedMandatoryVersions = mandatoryVersions(DependencyName("uk.gov.hmrc", "play-frontend"))

    parsedMandatoryVersions.size shouldBe 2
    parsedMandatoryVersions(1).range shouldBe VersionRange(Some(Version("8.0.0")),true, Some(Version("8.4.1")), true)
    parsedMandatoryVersions(1).reason shouldBe "reason"
    parsedMandatoryVersions(1).from shouldBe new LocalDate(2015, 3, 1)
  }

  it("should get versions from Nexus search results"){

    Nexus.versionsFromNexus(xml) shouldBe Seq(Version(Seq("2", "2", "3-SNAP1")), Version(Seq("2", "2", "2")))
  }

  it("should recognise an early release"){
    Version.isEarlyRelease(Version(Seq("2", "2", "3-SNAP1"))) shouldBe true
    Version.isEarlyRelease(Version(Seq("2", "2", "2"))) shouldBe false
  }

  val xml = <searchNGResponse>
    <totalCount>65</totalCount>
    <from>-1</from>
    <count>-1</count>
    <tooManyResults>false</tooManyResults>
    <collapsed>false</collapsed>
    <repoDetails/>
    <data>
      <artifact>
        <groupId>org.scalatest</groupId>
        <artifactId>scalatest_2.11</artifactId>
        <version>2.2.3-SNAP1</version>
        <latestRelease>2.2.3-SNAP1</latestRelease>
        <latestReleaseRepositoryId>sonatype-oss-releases</latestReleaseRepositoryId>
        <artifactHits>
          <artifactHit>
            <repositoryId>sonatype-oss-releases</repositoryId>
            <artifactLinks>
              <artifactLink>
                <extension>pom</extension>
              </artifactLink>
              <artifactLink>
                <extension>jar</extension>
              </artifactLink>
              <artifactLink>
                <classifier>sources</classifier>
                <extension>jar</extension>
              </artifactLink>
              <artifactLink>
                <classifier>javadoc</classifier>
                <extension>jar</extension>
              </artifactLink>
            </artifactLinks>
          </artifactHit>
        </artifactHits>
      </artifact>
      <artifact>
        <groupId>org.scalatest</groupId>
        <artifactId>scalatest_2.11</artifactId>
        <version>2.2.2</version>
        <latestRelease>2.2.3-SNAP1</latestRelease>
        <latestReleaseRepositoryId>sonatype-oss-releases</latestReleaseRepositoryId>
        <artifactHits>
          <artifactHit>
            <repositoryId>sonatype-oss-releases</repositoryId>
            <artifactLinks>
              <artifactLink>
                <extension>pom</extension>
              </artifactLink>
              <artifactLink>
                <extension>jar</extension>
              </artifactLink>
              <artifactLink>
                <classifier>sources</classifier>
                <extension>jar</extension>
              </artifactLink>
              <artifactLink>
                <classifier>javadoc</classifier>
                <extension>jar</extension>
              </artifactLink>
            </artifactLinks>
          </artifactHit>
        </artifactHits>
      </artifact>
    </data>
    </searchNGResponse>
}
