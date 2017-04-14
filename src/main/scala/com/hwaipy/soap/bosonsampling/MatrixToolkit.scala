package com.hwaipy.soap.bosonsampling

//import java.io.File
import org.jscience.mathematics.vector.{ComplexMatrix, ComplexVector}
import org.jscience.mathematics.number.Complex

//import scala.io.Source
import scala.util.Random
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

object JScienceUtil {

  implicit class ComplexMatrixImp(m: ComplexMatrix) {
    def mapOne(row: Int, column: Int, action: Complex => Complex) = {
      val data = Range(0, m.getNumberOfRows).map(row => Range(0, m.getNumberOfColumns).map(column => m.get(row, column)).toArray).toArray
      data(row)(column) = action(data(row)(column))
      ComplexMatrix.valueOf(data)
    }

    def mapAll(action: (Complex, Int, Int) => Complex) = {
      val data = Range(0, m.getNumberOfRows).map(row => Range(0, m.getNumberOfColumns).map(column => action(m.get(row, column), row, column)).toArray).toArray
      ComplexMatrix.valueOf(data)
    }

    def dag = m.mapAll((c, row, column) => c.conjugate).transpose

    def elementSum = Range(0, m.getNumberOfRows).map(row => Range(0, m.getNumberOfColumns).map(column => m.get(row, column)).toList.fold(Complex.ZERO) { (a, b) => a.plus(b) }).toList.fold(Complex.ZERO) { (a, b) => a.plus(b) }

    def unitarility = {
      val UdagUMod = m.times(m.dag).mapAll((c, row, column) => Complex.valueOf(c.magnitude, 0))
      //    val UdagUMod = m.dag.times(m).mapAll((c, row, column) => Complex.valueOf(c.magnitude, 0))
      val diag = ComplexMatrix.valueOf(UdagUMod.getDiagonal)
      val diagSum = diag.elementSum
      val UdagUModSum = UdagUMod.elementSum
      diagSum.getReal / UdagUModSum.getReal
    }

    def shake = {
      val random = new Random
      m.mapOne(random.nextInt(m.getNumberOfRows), random.nextInt(m.getNumberOfColumns), c => {
        val move = random.nextGaussian * 0.1
        random.nextBoolean match {
          case true => Complex.valueOf(c.getReal + move, c.getImaginary)
          case false => Complex.valueOf(c.getReal, c.getImaginary + move)
        }
      })
    }

    protected def subMatrixB(deleteIndex: Int, isRow: Boolean): ComplexMatrix = {
      val matrix = isRow match {
        case true => m
        case false => m.transpose
      }
      val m2 = ComplexMatrix.valueOf(Range(0, matrix.getNumberOfRows).filter(i => i != deleteIndex).map(i => matrix.getRow(i)))
      isRow match {
        case true => m2
        case false => m2.transpose
      }
    }

    protected def subMatrix(deleteRow: Int, deleteColumn: Int): ComplexMatrix = {
      val m1 = m.subMatrixB(deleteRow, true)
      val m2 = m1.subMatrixB(deleteColumn, false)
      m2
    }

    protected def doPerm: Complex = {
      m.getNumberOfRows match {
        case 0 => throw new RuntimeException
        case 1 => m.get(0, 0)
        case 2 => m.get(0, 0).times(m.get(1, 1)).plus(m.get(0, 1).times(m.get(1, 0)))
        case _ => {
          var p = Complex.ZERO
          for (i <- 0 until m.getNumberOfColumns) {
            val key = m.get(0, i)
            val subM = m.subMatrix(0, i)
            val subPerm = subM.doPerm
            p = p.plus(key.times(subPerm))
          }
          p
        }
      }
    }

    protected def calculatePermenent(arrange: List[Int]) = {
      val subMode = m.getNumberOfRows
      val columns = arrange.map(cn => m.getColumn(cn - 1))
      val subM = ComplexMatrix.valueOf(columns).transpose
      val perm = subM.doPerm
      val permModSquare = math.pow(perm.getReal, 2) + math.pow(perm.getImaginary, 2)
      permModSquare
    }

    protected def doCalculatePermenents = {
      val mode = m.getNumberOfColumns
      val subMode = m.getNumberOfRows
      val arranges = listArranges(1, mode + 1, subMode)
      val perms = arranges.map(m.calculatePermenent)
      val sum = perms.reduce { (a, b) => a + b }
      val permsNorm = perms.map(p => p / sum)
      permsNorm
    }

    def permenents(inputs: List[Int]) = {
      val rows = inputs.map(i => m.getRow(i))
      val subMode = rows.size
      val permM = ComplexMatrix.valueOf(rows)
      val permenents = permM.doCalculatePermenents
      permenents
    }

    def normalRow = {
      val rows = Range(0, m.getNumberOfRows).map(m.getRow(_).normal)
      ComplexMatrix.valueOf(rows)
    }
  }

  implicit class ComplexVectorImp(v: ComplexVector) {
    def normal = {
      val items = toList
      val sum = items.map(c => math.pow(c.magnitude, 2)).reduce { (a, b) => a + b }
      val normaledItems = items.map(c => c.divide(math.sqrt(sum)))
      ComplexVector.valueOf(normaledItems)
    }

    def toList = {
      Range(0, v.getDimension).map(v.get(_)).toList
    }
  }

  def listArranges(from: Int, to: Int, deepth: Int): List[List[Int]] = {
    def doListArranges(from: Int, to: Int, deepth: Int): ListBuffer[ListBuffer[Int]] = {
      val arranges = new ListBuffer[ListBuffer[Int]]()
      for (current <- from until to) {
        deepth > 1 match {
          case true => {
            val nexts = doListArranges(current + 1, to, deepth - 1)
            nexts.foreach(next => next.insert(0, current))
            nexts.foreach(next => arranges += next)
          }
          case false => {
            val list = new ListBuffer[Int]()
            list += current
            arranges += list
          }
        }
      }
      arranges
    }
    doListArranges(from, to, deepth).map(_.toList).toList
  }

  def normal(v: List[Double]) = {
    val sum = v.reduce { (a, b) => a + b }
    v.map(i => i / sum)
  }

  def similarity(permenents: List[Double], sampling: List[Double]) = {
    if (permenents.size != sampling.size) throw new RuntimeException
    val normalP = normal(permenents)
    val normalS = normal(sampling)
    normalP.zip(normalS).map(z => math.sqrt(z._1 * z._2)).reduce { (a, b) => a + b }
  }

  def distance(permenents: List[Double], sampling: List[Double]) = {
    if (permenents.size != sampling.size) throw new RuntimeException
    val normalP = normal(permenents)
    val normalS = normal(sampling)
    normalP.zip(normalS).map(z => math.abs(z._1 - z._2)).reduce { (a, b) => a + b }
  }

  class SamplingResult(results: Map[String, Tuple2[List[Double], List[Int]]], matrix: ComplexMatrix) {
    val distances = results.map(z => {
      val perm = matrix.permenents(z._2._2)
      val sampling = z._2._1
      (z._1, distance(perm, sampling))
    })
    val similarities = results.map(z => {
      val perm = matrix.permenents(z._2._2)
      val sampling = z._2._1
      (z._1, similarity(perm, sampling))
    })
    val summary = results.keys.toList.sorted.map(key => s"$key: distance = ${distances(key)}, similarity = ${similarities(key)}")
  }

  //class Valuer(originalMatrix: ComplexMatrix, results: Map[String, Tuple2[List[Double], List[Int]]]) {
  //  private var previousMatrix = originalMatrix
  //
  //  def value(currentMatrix: ComplexMatrix) = {
  //    valueSimilarToSamplingResultFromPolDecdMatrix(currentMatrix)
  //  }
  //
  //  def update(newMatrix: ComplexMatrix) = {
  //    previousMatrix = newMatrix
  //  }
  //
  //  def finished = previousMatrix.unitarility > 0.99 && false
  //
  //  private def valueNewMatrixOnly(newMatrix: ComplexMatrix) = {
  //    val preU = previousMatrix.unitarility
  //    val newU = newMatrix.unitarility
  //    newU >= preU
  //  }
  //
  //  private def valueSimilarToOriginalMatrix(newMatrix: ComplexMatrix) = {
  //    def calc(m: ComplexMatrix) = {
  //      val tr = originalMatrix.times(m.dag).getDiagonal.toList.map(c => math.pow(c.magnitude, 2)).reduce { (a, b) => a + b }
  //      val uni = m.unitarility
  //      (tr, uni)
  //    }
  //    val vPre = calc(previousMatrix)
  //    val vNew = calc(newMatrix)
  //    val deltaUni = vNew._2 - vPre._2
  //    val deltaTr = vNew._1 - vPre._1
  //    val accept = (deltaUni >= 0) && (deltaTr > -(deltaUni) / 4)
  //    if (accept) {
  //      println(s"VALID: uni = ${vNew._2}, Tr = ${vNew._1}")
  //    }
  //    accept
  //  }
  //
  //  private def valueSimilarToSamplingResult(newMatrix: ComplexMatrix) = {
  //    def calc(m: ComplexMatrix) = {
  //      val uni = m.unitarility
  //      val dists = results.keys.toList.sorted.map(key => {
  //        val res = results(key)
  //        distance(m.permenents(res._2), res._1)
  //      })
  //      (dists, uni)
  //    }
  //    val vPre = calc(previousMatrix)
  //    val vNew = calc(newMatrix)
  //    val deltaUni = vNew._2 - vPre._2
  //    val deltaDists = vNew._1.zip(vPre._1).map(z => z._1 - z._2)
  //    val acceptable = (deltaUni >= 0) && (deltaDists.forall(d => d <= 0))
  //    (acceptable, -deltaDists(0) - deltaDists(1), deltaUni)
  //  }
  //
  //  private def valueSimilarToSamplingResultFromPolDecdMatrix(newMatrix: ComplexMatrix) = {
  //    def calc(m: ComplexMatrix) = {
  //      val uni = m.unitarility
  //      val dists = results.keys.toList.sorted.map(key => {
  //        val res = results(key)
  //        //                distance(m.permenents(res._2), res._1)
  //        -similarity(m.permenents(res._2), res._1)
  //      })
  //      (dists, uni)
  //    }
  //    val vPre = calc(previousMatrix)
  //    val vNew = calc(newMatrix)
  //    val deltaUni = vNew._2 - vPre._2
  //    val deltaDists = vNew._1.zip(vPre._1).map(z => z._1 - z._2)
  //    val acceptable = (vNew._2 >= 0.9) //&& (deltaDists.forall(d => d <= 0))
  //    (acceptable, deltaUni > 0, -deltaDists(0) - deltaDists(1))
  //  }
  //}
  //
  //def loadOutputedMatrix(file: File) = {
  //  val mS = Source.fromFile(file).getLines.toList.mkString("")
  //  if (!mS.startsWith("{{") || !mS.endsWith("}}")) throw new RuntimeException
  //  ComplexMatrix.valueOf(mS.substring(2, mS.size - 2).split("\\},\\{").toList.map(line => line.split(",").toList.map(item => {
  //    val t = item.trim.split(" ", 2).toList
  //    val real = t(0).toDouble
  //    if (!t(1).endsWith("i")) throw new RuntimeException
  //    val image = t(1).subSequence(0, t(1).size - 1).toString.replaceAll(" ", "").toDouble
  //    Complex.valueOf(real, image)
  //  })).map(_.toArray).toArray).normalRow
  //}
  //
  //def search = {
  //  println(s"The search begins.")
  //  val matrixData = Source.fromFile(new File("Matrix.csv")).getLines.toList.filter(line => line.size > 0).map(line => line.split("[, \t]").map(item => item.trim.toDouble).toList)
  //  val mode = 9
  //  val matrix = ComplexMatrix.valueOf(matrixData.slice(0, mode).zip(matrixData.slice(mode, mode * 2)).map(pair => {
  //    pair._1.zip(pair._2).map(i => {
  //      val amp = i._1
  //      val pha = i._2
  //      val real = amp * math.cos(pha)
  //      val image = amp * math.sin(pha)
  //      Complex.valueOf(real, image)
  //    }).toArray
  //  }).toArray.slice(0, 5)).normalRow
  //  //  val matrix = loadOutputedMatrix(new File("matrixOutput.mat"))
  //  val samplingResults = Map(
  //    "3 Photons - 123" -> ("Result123.csv", 0 :: 1 :: 2 :: Nil),
  //    //    "3 Photons - 345" -> ("Result345.csv", 2 :: 3 :: 4 :: Nil)
  //    "4 Photons - 1235" -> ("Result1235.csv", 0 :: 1 :: 2 :: 4 :: Nil),
  //    "5 Photons - 12345" -> ("Result12345.csv", 0 :: 1 :: 2 :: 3 :: 4 :: Nil)
  //  ).map(z => z._1 -> {
  //    val s = Source.fromFile(new File(z._2._1), "UTF-8").getLines.toList.head.split(",").map(item => (item.trim.toDouble * 10000).toInt).toList
  //    val sum = s.reduce { (a, b) => a + b }.toDouble
  //    (s.map(i => (i / sum)), z._2._2)
  //  })
  //  println(s"Matrix and Sampling Results loaded.")
  //  println(s"Match of the sampling results with the original Matrix:")
  //  new SamplingResult(samplingResults, matrix).summary.foreach(line => println(s"    $line"))
  //
  //  var originalMatrix = matrix.normalRow
  //  var currentMatrix = matrix.normalRow
  //  val valuer = new Valuer(originalMatrix, samplingResults)
  //  var steps = 0
  //  var validSteps = 0
  //  while (!valuer.finished) {
  //    steps += 1
  //    println(s"start with U = ${currentMatrix.unitarility}")
  //    val valuedMatrix = Range(0, 3).map(i => {
  //      val newMatrix = currentMatrix.shake.normalRow
  //      val value = valuer.value(newMatrix)
  //      println(value)
  //      (value, newMatrix)
  //    }).toList.sortBy(_._1).reverse.head
  //    if (valuedMatrix._1._1) {
  //      valuer.update(valuedMatrix._2)
  //      currentMatrix = valuedMatrix._2
  //      validSteps += 1
  //      println(valuedMatrix._1)
  //      println(valuedMatrix._2.unitarility)
  //      println(valuedMatrix._2)
  //      new SamplingResult(samplingResults, currentMatrix).summary.foreach(line => println(s"$line"))
  //    }
  //    println(s"$steps, $validSteps")
  //
  //
  //    //    valuer.update(newMatrix) match {
  //    //      case true => {
  //    //        currentMatrix = newMatrix
  //    //        validSteps += 1
  //    //      }
  //    //      case false =>
  //    //    }
  //  }
  //
  //  println(s"Search finished in ${steps} steps (${validSteps} valid).")
  //  println(s"Match of the sampling results with the transformed Matrix:")
  //  new SamplingResult(samplingResults, currentMatrix).summary.foreach(line => println(s"    $line"))
  //}
  //
  //def varify = {
  //  val matrix = loadOutputedMatrix(new File("Sec/R2_4and5.mat"))
  //  val samplingResults = Map(
  //    //    "3 Photons - 123" -> ("Result123.csv", 0 :: 1 :: 2 :: Nil),
  //    "4 Photons - 1235" -> ("Sec/1235.csv", 0 :: 1 :: 2 :: 4 :: Nil),
  //    "5 Photons - 12345" -> ("Result12345.csv", 0 :: 1 :: 2 :: 3 :: 4 :: Nil)
  //  ).map(z => z._1 -> {
  //    val s = Source.fromFile(new File(z._2._1)).getLines.toList.head.split(",").map(item => (item.trim.toDouble * 10000).toInt).toList
  //    val sum = s.reduce { (a, b) => a + b }.toDouble
  //    (s.map(i => (i / sum)), z._2._2)
  //  })
  //  new SamplingResult(samplingResults, matrix).summary.foreach(line => println(s"    $line"))
  //
  //  val vm = Range(0, matrix.getNumberOfRows).toList.map(row => {
  //    Range(0, matrix.getNumberOfColumns).toList.map(column => {
  //      val c = matrix.get(row, column)
  //      val real = c.getReal
  //      val imaginary = c.getImaginary
  //      val amp = c.magnitude
  //      val pha = math.atan(imaginary / real) match {
  //        case pa if real >= 0 => pa
  //        case pa => pa + math.Pi
  //      }
  //      val realCheck = math.abs(amp * math.cos(pha) - real)
  //      val imaginaryCheck = math.abs(amp * math.sin(pha) - imaginary)
  //      if (realCheck > 0.0001 || imaginaryCheck > 0.0001) {
  //        println(s"$row, $column")
  //        println(c)
  //        println(real)
  //        println(imaginary)
  //        println("---")
  //        println(amp)
  //        println(pha)
  //        println("---")
  //        println(amp * math.cos(pha))
  //        println(amp * math.sin(pha))
  //        throw new RuntimeException
  //      }
  //      (amp, pha)
  //    })
  //  })
  //  val refPha = vm.get(2).map(item => item._2)
  //  val vmRD = vm.map(line => line.zip(refPha).map(item => (item._1._1, item._1._2 - item._2)))
  //  vmRD.foreach(line => {
  //    line.foreach(pair => {
  //      print(pair._1 + ", ")
  //    })
  //    println
  //  })
  //  vmRD.foreach(line => {
  //    line.foreach(pair => {
  //      print(pair._2 + ", ")
  //    })
  //    println
  //  })
  //
  //  println("---------------------------------------------------")
  //  Range(0, matrix.getNumberOfRows).toList.map(row => {
  //    Range(0, matrix.getNumberOfColumns).toList.map(column => {
  //      val c = matrix.get(row, column)
  //      val real = c.getReal
  //      val imaginary = c.getImaginary
  //      print(s"$real, ")
  //    })
  //    println
  //  })
  //  println()
  //  Range(0, matrix.getNumberOfRows).toList.map(row => {
  //    Range(0, matrix.getNumberOfColumns).toList.map(column => {
  //      val c = matrix.get(row, column)
  //      val real = c.getReal
  //      val imaginary = c.getImaginary
  //      print(s"$imaginary, ")
  //    })
  //    println
  //  })
  //}
  //
  ////search
  ////
  //varify
  //
  //Thread.sleep(200)

}
