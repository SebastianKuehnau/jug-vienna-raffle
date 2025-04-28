package com.vaadin.demo.application.adapter.out.meetupclient;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class MeetupMockData {

    public final static String eventsDocument = """
                query($urlname: String!) {
                    groupByUrlname(urlname: $urlname) {
                    	events {
                    	  edges {
                    	    node {
                    	      id
                            title
                            dateTime
                            description
                    	    }
                    	  }
                    	}
                    }
                  }
            """;

    public final static String events = """
                {
                  "data": {
                    "groupByUrlname": {
                      "events": {
                        "edges": [
                          {
                            "node": {
                              "id": "305897200",
                              "token": "305897200",
                              "title": "\\"JVMTI: what's the JVM Tool Interface\\" + \\"Fun with Flags: OpenFeature\\"",
                              "dateTime": "2025-05-05T18:00:00+02:00",
                              "description": "Agenda\\n\\n18:00 doors Open\\n18:20 Welcome\\n18:30 **JVMTI: Introduction and use cases for the JVM Tool Interface** - *Kevin Watzal*\\n19:30 Break/Networking\\n19:55 Raffle: IntelliJ + OrbStack.dev Licenses\\n20:00 **Fun with Flags: Bringing the Fun Back into Feature Flagging with OpenFeature** - *Simon Schrottner*\\n21:15 Networking\\n\\n**JVMTI: Introduction and use cases for the JVM Tool Interface**\\nEver wondered how profilers track memory allocations or how they can measure the methods that use most of the CPU resources without knowing your code?\\n\\nMeet JVMTI — the Java Virtual Machine Tool Interface, that allows tracking, inspection and modification of JVM applications. Track the states of your threads, trace objects as they follow the flow of your application or modify instance values at runtime. Learn how JVMTI works, how profilers, debuggers and other tools rely on it and find interesting ways to solve issues of your application. Lastly, see how security researchers monitor JVM applications to help protect users and organizations.\\n\\n**About the Speaker**\\nKevin Watzal is a Software Developer with a passion for the JVM for nearly 10 years. He currently works in the e-mobility sector, specializing in fleet management and electric vehicle charging solutions.\\n\\nHis motto, “Knowledge is power,” reflects his enthusiasm for exploring new libraries and frameworks to continuously discover innovative problem-solving techniques. Alongside his professional work, Kevin pursued part-time studies in IT Security, with a master’s thesis that focused on the security analysis of JVM applications using JVMTI.\\n\\nIn addition to his expertise in software development, Kevin is also passionate about sustainability and nutrition.\\n\\n**Fun with Flags: Bringing the Fun Back into Feature Flagging with OpenFeature**\\nFeature flags have revolutionized the software delivery lifecycle, enabling teams to decouple releases from deployments and create a more agile development process. They're often hailed as one of the key practices in modern software development—at least in theory.\\n\\nHowever, as systems grow in complexity, so do the challenges associated with feature flagging. From supporting multiple languages and managing targeted evaluations to avoiding vendor lock-in and safely decommissioning obsolete code, what initially seems like a straightforward problem can quickly become daunting.\\n\\nThe OpenFeature community is tackling these challenges head-on by providing vendor-agnostic SDKs and a suite of powerful tools designed to simplify the feature flagging experience. Join me as we explore the common pitfalls of feature flagging and discover how OpenFeature can help bring the fun back into this critical aspect of software development.\\n\\n**About the Speaker**\\nTBA"
                            }
                          },
                          {
                            "node": {
                              "id": "306898838",
                              "token": "306898838",
                              "title": "\\"Engineering a better Java build tool\\" + \\"Vaadin 24 in the real world\\"",
                              "dateTime": "2025-05-19T18:00:00+02:00",
                              "description": "Joint Meetup with [Scala Vienna](https://www.meetup.com/scala-Vienna/)\\n\\nAgenda:\\n18:00 doors open\\n18:20 organizational stuff\\n18:30 - 19:30 Mill: Untapped Potential in the Java Build Tool Experience - Li Haoyi\\n19:30 - 19:55 Pizza break\\n19:55 - 20:00 Raffle\\n20:00 - 21:00 Vaadin 24 in the real world - building the Java Vienna Raffle! - Sebastian Kühnau\\n\\n**Untapped Potential in the Java Build Tool Experience**\\nThe Java language is known to be fast, safe, and easy, but Java build tools like Maven or Gradle sometimes don't quite live up to that standard. This talk will explore what \\"could be\\": where current Java build tools fall behind modern build tools in other communities, in performance, extensibility, and ease of getting started. We will end with a demonstration of an experimental build tool \\"Mill\\" that makes use of these ideas, proving out the idea that Java build tooling has the potential to be much faster, safer, and easier than it is today.\\n\\n**About the Speaker**\\n**[Li Haoyi](https://github.com/lihaoyi)** graduated from MIT with a degree in Computer Science and Engineering, and since then has been a major contributor to the open source community. His projects have over 10,000 stars on Github, and are downloaded over 20,000,000 times a month. Haoyi professionally built distributed backend systems, programming languages, high-performance web applications, and much more.\\n\\n**Vaadin 24 in the real world - building the Java Vienna Raffle**\\nTBA\\n\\n**About the Speaker**\\n**[Sebastian Kühnau](https://github.com/SebastianKuehnau)** ist seit zwei Jahrzehnten Java-Junkie und schon während seines Studiums Vaadin verfallen. Seit 2016 arbeitet er in verschiedenen Tech-Rollen bei Vaadin und unterstützt Kunden, maßgeschneiderte Webanwendungen zu entwickeln. Nebenher ist er zudem auf verschiedenen Events in und mit der Community aktiv."
                            }
                          },
                          {
                            "node": {
                              "id": "305897255",
                              "token": "305897255",
                              "title": "\\"OpenRewrite in a Nutshell\\" + \\"how John started to like TDD (w/ spring boot)\\"",
                              "dateTime": "2025-06-02T18:00:00+02:00",
                              "description": "save the date!\\n\\nAgenda:\\n18:00 doors open\\n18:20 welcome\\n18:30 OpenRewrite in a Nutshell: Scaling Upgrades with Practical Insights - Simon Gartner\\n19:30 break + networking\\n19:55 raffle\\n20:00 how John started to like TDD (instead of hating it) - Nacho Cougil\\n21:15 networking\\n\\n**OpenRewrite in a Nutshell: Scaling Upgrades with Practical Insights**\\nSchnelle Upgrades, einfache Rezepte:\\n\\n* **Was ist OpenRewrite?** Kurz & knackig.\\n* **Rezepte anwenden:** Composite Recipes, dryRun vs. run\\n* **Tech-Upgrades:** Wenig Downtime, klare Nachvollziehbarkeit (z.B. EAP-8)\\n* **Lesende Rezepte:** Code-Analyse leicht gemacht\\n* **Rezepte schreiben:** LSTs, Visitors, Praxisbeispiel\\n* **Rezepte wiederverwenden:** EAP-8, Quarkus, Spring Microservices\\n\\nIdeal für effiziente, transparente Software-Upgrades.\\n\\n**About the Speaker:**\\n**Simon Gartner - Softwareentwickler @ [Gepardec IT Services GmbH](https://www.gepardec.com/).**\\nSimon ist Softwareentwickler mit viel Erfahrung mit Legacy-Code. Er beschäftigt sich in seinen Projekten intensiv mit der Anwendung von OpenRewrite in der Praxis. Sein Fokus liegt bei der automatisierten und reproduzierbaren Durchführung von Migrationen. Simon konnte mehrere Modernisierungsprojekte, wie Frameworkupgrades, effizient und innovativ meisten.\\n\\n**how John started to like TDD (instead of hating it)**\\nLet me share a story about how John (a developer like any other) started to understand (and enjoy) writing Tests before the Production code.\\n\\nWe've all felt an inevitable \\"tedium\\" when writing tests, haven't we? If it's boring, if it's complicated or unnecessary? Isn't it? John thought so too, and, as much as he had heard about writing tests before production code, he had never managed to put it into practice, and even when he had tried, John had become even more frustrated at not understanding how to put it into practice outside of a few examples katas 🤷‍♂️\\n\\nListen to this story in which I will explain how John went from not understanding Test Driven Development (TDD) to being passionate about it... so much that now he doesn't want to work any other way 😅 ! He must have found some benefits in practising it, right? He says he has more advantages than working in any other way (e.g., you'll find defects earlier, you'll have a faster feedback loop or your code will be easier to refactor), but I'd better explain it to you in the session, right? Ah! And if you think everything will be theoretical, no! **Get ready to see code examples with Spring Boot**.\\n\\nPS: Think of John as a random person, as if he was even the speaker of this talk 😉 !\\n\\n**About the Speaker**\\n**[Nacho Cougil](https://github.com/icougil)** is a software engineer from Barcelona, fan of TDD and XP practices. He has been working with Java and other web technologies before the effect 2000 and had experience in different roles in the IT world now working at Dynatrace writing code to monitor applications. You may probably meet him before as founder of the Barcelona Java Users Group & the Java & JVM Barcelona Conference ( JBCNConf ). He enjoys spending time with his family, playing sports & improving his eXtreme Programming skills.\\nNacho is the founder of the [Barcelona Java User Group](https://github.com/barcelonajug) and a Java Champion."
                            }
                          },
                          {
                            "node": {
                              "id": "305897281",
                              "token": "305897281",
                              "title": "Java on AWS Special",
                              "dateTime": "2025-06-16T18:00:00+02:00",
                              "description": "Joint meetup with [AWS Vienna Meetup](https://www.meetup.com/amazon-web-services-aws-vienna/) !\\n\\nAgenda\\n\\n18:00 doors open\\n18:20 welcome\\n18:30 Talk 1\\n19:30 Break\\n19:55 Raffle\\n20:00 Talk 2\\n21:15 Networking nearby"
                            }
                          }
                        ]
                      }
                    }
                  }
                }
            """;

    public final static String eventDocument = """
                query($id: ID!) {
                    event(id: $id) {
                    	id
                    	token
                    	title
                  	  dateTime
                   	 	description
                  	}
                  }
            """;

    public final static Map<String, String> eventMap = ImmutableMap.of("305897200", """
                {
                  "data": {
                    "event": {
                      "id": "305897200",
                      "token": "305897200",
                      "title": "\\"JVMTI: what's the JVM Tool Interface\\" + \\"Fun with Flags: OpenFeature\\"",
                      "dateTime": "2025-05-05T18:00:00+02:00",
                      "description": "Agenda\\n\\n18:00 doors Open\\n18:20 Welcome\\n18:30 **JVMTI: Introduction and use cases for the JVM Tool Interface** - *Kevin Watzal*\\n19:30 Break/Networking\\n19:55 Raffle: IntelliJ + OrbStack.dev Licenses\\n20:00 **Fun with Flags: Bringing the Fun Back into Feature Flagging with OpenFeature** - *Simon Schrottner*\\n21:15 Networking\\n\\n**JVMTI: Introduction and use cases for the JVM Tool Interface**\\nEver wondered how profilers track memory allocations or how they can measure the methods that use most of the CPU resources without knowing your code?\\n\\nMeet JVMTI — the Java Virtual Machine Tool Interface, that allows tracking, inspection and modification of JVM applications. Track the states of your threads, trace objects as they follow the flow of your application or modify instance values at runtime. Learn how JVMTI works, how profilers, debuggers and other tools rely on it and find interesting ways to solve issues of your application. Lastly, see how security researchers monitor JVM applications to help protect users and organizations.\\n\\n**About the Speaker**\\nKevin Watzal is a Software Developer with a passion for the JVM for nearly 10 years. He currently works in the e-mobility sector, specializing in fleet management and electric vehicle charging solutions.\\n\\nHis motto, “Knowledge is power,” reflects his enthusiasm for exploring new libraries and frameworks to continuously discover innovative problem-solving techniques. Alongside his professional work, Kevin pursued part-time studies in IT Security, with a master’s thesis that focused on the security analysis of JVM applications using JVMTI.\\n\\nIn addition to his expertise in software development, Kevin is also passionate about sustainability and nutrition.\\n\\n**Fun with Flags: Bringing the Fun Back into Feature Flagging with OpenFeature**\\nFeature flags have revolutionized the software delivery lifecycle, enabling teams to decouple releases from deployments and create a more agile development process. They're often hailed as one of the key practices in modern software development—at least in theory.\\n\\nHowever, as systems grow in complexity, so do the challenges associated with feature flagging. From supporting multiple languages and managing targeted evaluations to avoiding vendor lock-in and safely decommissioning obsolete code, what initially seems like a straightforward problem can quickly become daunting.\\n\\nThe OpenFeature community is tackling these challenges head-on by providing vendor-agnostic SDKs and a suite of powerful tools designed to simplify the feature flagging experience. Join me as we explore the common pitfalls of feature flagging and discover how OpenFeature can help bring the fun back into this critical aspect of software development.\\n\\n**About the Speaker**\\nTBA"
                    }
                  }
                }
            """, "306898838", """
                {
                  "data": {
                    "event": {
                      "id": "306898838",
                      "token": "306898838",
                      "title": "\\"Engineering a better Java build tool\\" + \\"Vaadin 24 in the real world\\"",
                      "dateTime": "2025-05-19T18:00:00+02:00",
                      "description": "Joint Meetup with [Scala Vienna](https://www.meetup.com/scala-Vienna/)\\n\\nAgenda:\\n18:00 doors open\\n18:20 organizational stuff\\n18:30 - 19:30 Mill: Untapped Potential in the Java Build Tool Experience - Li Haoyi\\n19:30 - 19:55 Pizza break\\n19:55 - 20:00 Raffle\\n20:00 - 21:00 Vaadin 24 in the real world - building the Java Vienna Raffle! - Sebastian Kühnau\\n\\n**Untapped Potential in the Java Build Tool Experience**\\nThe Java language is known to be fast, safe, and easy, but Java build tools like Maven or Gradle sometimes don't quite live up to that standard. This talk will explore what \\"could be\\": where current Java build tools fall behind modern build tools in other communities, in performance, extensibility, and ease of getting started. We will end with a demonstration of an experimental build tool \\"Mill\\" that makes use of these ideas, proving out the idea that Java build tooling has the potential to be much faster, safer, and easier than it is today.\\n\\n**About the Speaker**\\n**[Li Haoyi](https://github.com/lihaoyi)** graduated from MIT with a degree in Computer Science and Engineering, and since then has been a major contributor to the open source community. His projects have over 10,000 stars on Github, and are downloaded over 20,000,000 times a month. Haoyi professionally built distributed backend systems, programming languages, high-performance web applications, and much more.\\n\\n**Vaadin 24 in the real world - building the Java Vienna Raffle**\\nTBA\\n\\n**About the Speaker**\\n**[Sebastian Kühnau](https://github.com/SebastianKuehnau)** ist seit zwei Jahrzehnten Java-Junkie und schon während seines Studiums Vaadin verfallen. Seit 2016 arbeitet er in verschiedenen Tech-Rollen bei Vaadin und unterstützt Kunden, maßgeschneiderte Webanwendungen zu entwickeln. Nebenher ist er zudem auf verschiedenen Events in und mit der Community aktiv."
                    }
                  }
                }
            """, "305897255", """
            {
              "data": {
                "event": {
                  "id": "305897255",
                  "token": "305897255",
                  "title": "\\"OpenRewrite in a Nutshell\\" + \\"how John started to like TDD (w/ spring boot)\\"",
                  "dateTime": "2025-06-02T18:00:00+02:00",
                  "description": "save the date!\\n\\nAgenda:\\n18:00 doors open\\n18:20 welcome\\n18:30 OpenRewrite in a Nutshell: Scaling Upgrades with Practical Insights - Simon Gartner\\n19:30 break + networking\\n19:55 raffle\\n20:00 how John started to like TDD (instead of hating it) - Nacho Cougil\\n21:15 networking\\n\\n**OpenRewrite in a Nutshell: Scaling Upgrades with Practical Insights**\\nSchnelle Upgrades, einfache Rezepte:\\n\\n* **Was ist OpenRewrite?** Kurz & knackig.\\n* **Rezepte anwenden:** Composite Recipes, dryRun vs. run\\n* **Tech-Upgrades:** Wenig Downtime, klare Nachvollziehbarkeit (z.B. EAP-8)\\n* **Lesende Rezepte:** Code-Analyse leicht gemacht\\n* **Rezepte schreiben:** LSTs, Visitors, Praxisbeispiel\\n* **Rezepte wiederverwenden:** EAP-8, Quarkus, Spring Microservices\\n\\nIdeal für effiziente, transparente Software-Upgrades.\\n\\n**About the Speaker:**\\n**Simon Gartner - Softwareentwickler @ [Gepardec IT Services GmbH](https://www.gepardec.com/).**\\nSimon ist Softwareentwickler mit viel Erfahrung mit Legacy-Code. Er beschäftigt sich in seinen Projekten intensiv mit der Anwendung von OpenRewrite in der Praxis. Sein Fokus liegt bei der automatisierten und reproduzierbaren Durchführung von Migrationen. Simon konnte mehrere Modernisierungsprojekte, wie Frameworkupgrades, effizient und innovativ meisten.\\n\\n**how John started to like TDD (instead of hating it)**\\nLet me share a story about how John (a developer like any other) started to understand (and enjoy) writing Tests before the Production code.\\n\\nWe've all felt an inevitable \\"tedium\\" when writing tests, haven't we? If it's boring, if it's complicated or unnecessary? Isn't it? John thought so too, and, as much as he had heard about writing tests before production code, he had never managed to put it into practice, and even when he had tried, John had become even more frustrated at not understanding how to put it into practice outside of a few examples katas 🤷‍♂️\\n\\nListen to this story in which I will explain how John went from not understanding Test Driven Development (TDD) to being passionate about it... so much that now he doesn't want to work any other way 😅 ! He must have found some benefits in practising it, right? He says he has more advantages than working in any other way (e.g., you'll find defects earlier, you'll have a faster feedback loop or your code will be easier to refactor), but I'd better explain it to you in the session, right? Ah! And if you think everything will be theoretical, no! **Get ready to see code examples with Spring Boot**.\\n\\nPS: Think of John as a random person, as if he was even the speaker of this talk 😉 !\\n\\n**About the Speaker**\\n**[Nacho Cougil](https://github.com/icougil)** is a software engineer from Barcelona, fan of TDD and XP practices. He has been working with Java and other web technologies before the effect 2000 and had experience in different roles in the IT world now working at Dynatrace writing code to monitor applications. You may probably meet him before as founder of the Barcelona Java Users Group & the Java & JVM Barcelona Conference ( JBCNConf ). He enjoys spending time with his family, playing sports & improving his eXtreme Programming skills.\\nNacho is the founder of the [Barcelona Java User Group](https://github.com/barcelonajug) and a Java Champion."
                }
              }
            }
            """, "305897281", """
            {
              "data": {
                "event": {
                  "id": "305897281",
                  "token": "305897281",
                  "title": "Java on AWS Special",
                  "dateTime": "2025-06-16T18:00:00+02:00",
                  "description": "Joint meetup with [AWS Vienna Meetup](https://www.meetup.com/amazon-web-services-aws-vienna/) !\\n\\nAgenda\\n\\n18:00 doors open\\n18:20 welcome\\n18:30 Talk 1\\n19:30 Break\\n19:55 Raffle\\n20:00 Talk 2\\n21:15 Networking nearby"
                }
              }
            }
            """);
}
