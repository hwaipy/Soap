@echo off
java -classpath "D:\GitHub\Soap\target\scala-2.12\classes;D:\GitHub\Soap\target\scala-2.12\soap_2.12-0.2.0.jar;C:\Users\Administrator\.sbt\boot\scala-2.12.3\lib\scala-compiler.jar;C:\Users\Administrator\.sbt\boot\scala-2.12.3\lib\scala-library.jar;C:\Users\Administrator\.sbt\boot\scala-2.12.3\lib\scala-reflect.jar;C:\Users\Administrator\.ivy2\cache\org.scala-lang.modules\scala-xml_2.12\bundles\scala-xml_2.12-1.0.6.jar;C:\Users\Administrator\.ivy2\local\com.hwaipy\hydrogen_2.12\0.3.0\jars\hydrogen_2.12.jar;C:\Users\Administrator\.ivy2\cache\org.jscience\jscience\jars\jscience-4.3.1.jar;C:\Users\Administrator\.ivy2\cache\org.javolution\javolution\jars\javolution-5.2.3.jar;C:\Users\Administrator\.ivy2\cache\org.scalanlp\breeze_2.12\jars\breeze_2.12-0.13.1.jar;C:\Users\Administrator\.ivy2\cache\org.scalanlp\breeze-macros_2.12\jars\breeze-macros_2.12-0.13.1.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\core\jars\core-1.1.2.jar;C:\Users\Administrator\.ivy2\cache\net.sourceforge.f2j\arpack_combined_all\jars\arpack_combined_all-0.1.jar;C:\Users\Administrator\.ivy2\cache\net.sf.opencsv\opencsv\jars\opencsv-2.3.jar;C:\Users\Administrator\.ivy2\cache\com.github.rwl\jtransforms\jars\jtransforms-2.4.0.jar;C:\Users\Administrator\.ivy2\cache\junit\junit\jars\junit-4.8.2.jar;C:\Users\Administrator\.ivy2\cache\org.apache.commons\commons-math3\jars\commons-math3-3.6.1.jar;C:\Users\Administrator\.ivy2\cache\org.spire-math\spire_2.12\jars\spire_2.12-0.13.0.jar;C:\Users\Administrator\.ivy2\cache\org.spire-math\spire-macros_2.12\jars\spire-macros_2.12-0.13.0.jar;C:\Users\Administrator\.ivy2\cache\org.typelevel\machinist_2.12\jars\machinist_2.12-0.6.1.jar;C:\Users\Administrator\.ivy2\cache\com.chuusai\shapeless_2.12\bundles\shapeless_2.12-2.3.2.jar;C:\Users\Administrator\.ivy2\cache\org.typelevel\macro-compat_2.12\jars\macro-compat_2.12-1.1.1.jar;C:\Users\Administrator\.ivy2\cache\org.scalanlp\breeze-natives_2.12\jars\breeze-natives_2.12-0.13.1.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_ref-osx-x86_64\jars\netlib-native_ref-osx-x86_64-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\native_ref-java\jars\native_ref-java-1.1.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil\jniloader\jars\jniloader-1.1.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_ref-linux-x86_64\jars\netlib-native_ref-linux-x86_64-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_ref-linux-i686\jars\netlib-native_ref-linux-i686-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_ref-win-x86_64\jars\netlib-native_ref-win-x86_64-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_ref-win-i686\jars\netlib-native_ref-win-i686-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_ref-linux-armhf\jars\netlib-native_ref-linux-armhf-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_system-osx-x86_64\jars\netlib-native_system-osx-x86_64-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\native_system-java\jars\native_system-java-1.1.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_system-linux-x86_64\jars\netlib-native_system-linux-x86_64-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_system-linux-i686\jars\netlib-native_system-linux-i686-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_system-linux-armhf\jars\netlib-native_system-linux-armhf-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_system-win-x86_64\jars\netlib-native_system-win-x86_64-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\com.github.fommil.netlib\netlib-native_system-win-i686\jars\netlib-native_system-win-i686-1.1-natives.jar;C:\Users\Administrator\.ivy2\cache\javax.servlet\javax.servlet-api\jars\javax.servlet-api-3.1.0.jar;C:\Users\Administrator\.ivy2\cache\com.xeiam.xchart\xchart\jars\xchart-2.5.1.jar;C:\Users\Administrator\.ivy2\cache\org.rxtx\rxtx\jars\rxtx-2.1.7.jar;C:\Users\Administrator\.ivy2\local\com.hydra\sydra_2.12\0.6.0\jars\sydra_2.12.jar;C:\Users\Administrator\.ivy2\cache\org.msgpack\msgpack-core\bundles\msgpack-core-0.8.13.jar;C:\Users\Administrator\.ivy2\cache\org.msgpack\jackson-dataformat-msgpack\bundles\jackson-dataformat-msgpack-0.8.13.jar;C:\Users\Administrator\.ivy2\cache\com.fasterxml.jackson.core\jackson-databind\bundles\jackson-databind-2.7.1.jar;C:\Users\Administrator\.ivy2\cache\com.fasterxml.jackson.core\jackson-annotations\bundles\jackson-annotations-2.7.0.jar;C:\Users\Administrator\.ivy2\cache\com.fasterxml.jackson.core\jackson-core\bundles\jackson-core-2.7.1.jar;C:\Users\Administrator\.ivy2\cache\org.apache.logging.log4j\log4j-slf4j-impl\jars\log4j-slf4j-impl-2.4.jar;C:\Users\Administrator\.ivy2\cache\org.slf4j\slf4j-api\jars\slf4j-api-1.7.12.jar;C:\Users\Administrator\.ivy2\cache\org.apache.logging.log4j\log4j-api\jars\log4j-api-2.4.jar;C:\Users\Administrator\.ivy2\cache\org.apache.logging.log4j\log4j-core\jars\log4j-core-2.4.jar;C:\Users\Administrator\.ivy2\cache\io.netty\netty-all\jars\netty-all-4.1.4.Final.jar;C:\Users\Administrator\.ivy2\cache\io.spray\spray-json_2.12\bundles\spray-json_2.12-1.3.3.jar;C:\Users\Administrator\.ivy2\cache\org.pegdown\pegdown\jars\pegdown-1.6.0.jar;C:\Users\Administrator\.ivy2\cache\org.parboiled\parboiled-java\jars\parboiled-java-1.1.7.jar;C:\Users\Administrator\.ivy2\cache\org.parboiled\parboiled-core\jars\parboiled-core-1.1.7.jar;C:\Users\Administrator\.ivy2\cache\org.ow2.asm\asm\jars\asm-5.0.3.jar;C:\Users\Administrator\.ivy2\cache\org.ow2.asm\asm-tree\jars\asm-tree-5.0.3.jar;C:\Users\Administrator\.ivy2\cache\org.ow2.asm\asm-analysis\jars\asm-analysis-5.0.3.jar;C:\Users\Administrator\.ivy2\cache\org.ow2.asm\asm-util\jars\asm-util-5.0.3.jar" com.hwaipy.soap.build.scalah.ScalaH %1 %2