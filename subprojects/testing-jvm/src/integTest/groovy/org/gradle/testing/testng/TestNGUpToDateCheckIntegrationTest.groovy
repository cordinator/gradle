/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.testing.testng

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.testing.fixture.TestNGCoverage
import spock.lang.Issue
import spock.lang.Unroll

class TestNGUpToDateCheckIntegrationTest extends AbstractIntegrationSpec {

    def setup() {
        executer.noExtraLogging()
        file('src/test/java/SomeTest.java') << '''
            public class SomeTest {
                @org.testng.annotations.Test
                public void pass() {}
            }
        '''.stripIndent()
        file('suite.xml') << '''
            <!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
            <suite name="MySuite">
              <test name="MyTest">
                <classes>
                  <class name="SomeTest" />
                </classes>
              </test>
            </suite>
        '''.stripIndent()
    }

    def 'test task is up-to-date when nothing has changed'() {
        given:
        TestNGCoverage.enableTestNG(buildFile)

        when:
        succeeds ':test'

        then:
        executedAndNotSkipped ':test'

        when:
        succeeds ':test'

        then:
        skipped ':test'
    }

    @Unroll
    @Issue('https://github.com/gradle/gradle/issues/4924')
    def "re-executes test when #property is changed"() {
        given:
        buildScript """
            apply plugin: "java"
            ${jcenterRepository()}
            dependencies { testCompile "org.testng:testng:${TestNGCoverage.NEWEST}" }
            test {
                useTestNG {
                    $property $modification
                }
            }
        """

        when:
        succeeds ':test'

        then:
        executedAndNotSkipped ':test'

        when:
        buildScript """
            apply plugin: "java"
            ${jcenterRepository()}
            dependencies { testCompile "org.testng:testng:${TestNGCoverage.NEWEST}" }
            test {
                useTestNG()
            }
        """

        and:
        succeeds ':test'

        then:
        executedAndNotSkipped ':test'

        where:
        property              | modification
        'parallel'            | '= "methods"'
        'threadCount'         | '= 2'
        'listeners'           | '= ["org.testng.reporters.FailedReporter"]'
        'configFailurePolicy' | '= "continue"'
        'useDefaultListeners' | '= true'
        'suiteName'           | '= "Honeymoon Suite"'
        'testName'            | '= "Turing completeness"'
        'preserveOrder'       | '= true'
        'groupByInstances'    | '= true'
        'excludeGroups'       | '= ["some group"]'
        'includeGroups'       | '= ["some group"]'
        'outputDirectory'     | '= file("$buildDir/my-out")'
        'suiteXmlFiles'       | '= [file("suite.xml")]'
        'suiteXmlBuilder()'   | '''
                                .suite(name: 'MySuite') {
                                    test(name: 'MyTest') {
                                        classes([:]) {
                                            'class'(name: 'SomeTest')
                                        }
                                    }
                                }
                                '''
    }

}
