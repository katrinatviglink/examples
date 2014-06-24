resolvers += Resolver.mavenLocal

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += 
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
	"com.viglink" 	%  "scalanlp_epic" 		% "0.2-SNAPSHOT" exclude ("com.typesafe.sbt", "sbt-pgp"),
	"org.scalanlp" 	%% "epic-parser-en-span" % "2014.6.3-SNAPSHOT" exclude ("com.typesafe.sbt", "sbt-pgp"),
	//"org.scalanlp" 	%% "epic-pos-en" 		% "2014.6.3-SNAPSHOT" exclude ("com.typesafe.sbt", "sbt-pgp"),
	"org.scalanlp" 	%% "epic-ner-en-conll" 	% "2014.6.3-SNAPSHOT" exclude ("com.typesafe.sbt", "sbt-pgp")
	//"org.scalanlp" 	%% "breeze"  			% "0.8.1"
	//"org.scalanlp" 	%% "nak"  				% "1.2.1"
	)
