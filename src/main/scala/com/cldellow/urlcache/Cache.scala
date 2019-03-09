package com.cldellow.urlcache

import java.io._
import java.net._
import net.openhft.hashing._
import org.apache.commons.io.IOUtils

class Cache(root: File) {
  root.mkdirs()
  private val xx = LongHashFunction.xx()
  private val murmur = LongHashFunction.murmur_3()

  private def hash(input: String): String = {
    def pad(x: String): String = ("0" * (16 - x.length)) + x
    pad(xx.hashChars(input).toHexString) + pad(murmur.hashChars(input).toHexString)
  }

  // Fetch a URL; store it in some hashed location.
  //
  // Most people should use this.
  def fetch(url: String): Array[Byte] = fetch(url, null)

  // Fetch a URL, store it in a stable location based on its name.
  //
  // This makes it easy to invalidate the cache by deleting it using normal
  // tools like `rm`.
  def fetchNamed(url: String): Array[Byte] =
    fetch(url, prefix = "misc", name = url.toLowerCase.replaceAll("[^a-z0-9.]", "_").replaceAll("_+", "_"))

  def fetch(url: String, range: (Int, Int) = null, prefix: String = null, name: String = null): Array[Byte] = {
    val filehash = hash(url)
    val filehashDir = filehash.take(2) + "/" + filehash.drop(2).take(2)

    val dirPrefix = List(Option(prefix), if(name == null) Some(filehashDir) else None).flatten
    val fname = Option(name).getOrElse(filehash) +
      Option(range).map { case (start, len) => "-" + start + "-" + len }.getOrElse("")

    if(dirPrefix.nonEmpty)
      new File(root, dirPrefix.mkString("/")).mkdirs()

    val finalf = new File(root, (dirPrefix ++ List(fname)).mkString("/"))
    if(finalf.exists)
      return IOUtils.toByteArray(new FileInputStream(finalf))


    val conn = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
    Option(range).foreach { case (start, len) =>
      conn.setRequestProperty("Range", s"bytes=${start}-${start + len - 1}")
    }

    val bytes =
      try {
        IOUtils.toByteArray(conn.getInputStream)
      } catch {
        case e: FileNotFoundException => new Array[Byte](0)
      }

    val tmpf = new File(root, "_" + filehash)
    val fos = new BufferedOutputStream(new FileOutputStream(tmpf))
    bytes.foreach { b => fos.write(b) }
    fos.flush()
    fos.close()
    tmpf.renameTo(finalf)
    bytes
  }
}

object Cache extends Cache(new File("/tmp/cache"))
