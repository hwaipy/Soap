import java.util.concurrent.atomic.AtomicInteger

import com.hydra.hap.SydraAppHandler
import com.hydra.io.MessageClient

import scala.io.Source

object MultiClient extends App {
  val clients = Range(0, 10).map(i => {
    val clientName = s"Test-$i"
    MessageClient.newClient("localhost", 20102, s"Test-$i", new SydraAppHandler(clientName, "doc.md") {
      val invokeCount = new AtomicInteger(0)

      override def getSummary() = {
        //        (<html>
        //          <h1>
        //            {clientName}
        //          </h1>
        //          <p></p>
        //          <p>running...</p>
        //          <p>
        //            {invokeCount.getAndIncrement}
        //          </p>
        //        </html>).toString
        s"""#$clientName
            |running...
            |${invokeCount.getAndIncrement}
      """.stripMargin
      }
    })
  })
  println(s"Clients connected.")
  Source.stdin.getLines.filter(line => line.toLowerCase == "q").next
  println("Stoping...")
  clients.foreach(c => c.stop)
}