package thu.ailab.document

import scala.annotation.tailrec
import thu.ailab.global.{ LoggerTrait, MyConfigFactory }
import thu.ailab.utils.MyCharsetDetector

/**
 * Document class, representing one HTML document, with some useful information.
 * It is *NOT* DOM or SAX, though.
 *
 * Important fields:
 *  - charset
 *  - simplifiedContent(strippedLines)
 */

abstract class HTMLDocument {
  /**
   * Abstract members
   */
  val charset: String 
  val fullContent: String
  /**
   *  Since we mainly deal with Chinese documents, default is gb18030
   */  
	val defaultCharset = "gb18030"
  /**
   * Remove tags according to the regular expression generator
   */
  private def removeTags(tags: List[String], content: String)(regexGen: (String) => String) = {
    @tailrec
    def removeTagsImpl(tags: List[String], content: String): String = {
      def removeTag(tag: String) = content.replaceAll(regexGen(tag), "")
      tags match {
        case Nil => content
        case tag :: rest => removeTagsImpl(rest, removeTag(tag))
      }
    }
    removeTagsImpl(tags, content)
  }
  /**
   * Remove paired tags, like <script>...</script>
   */
  private def removePairedTags(tags: List[String])(content: String) = {
    removeTags(tags, content)((tag: String) => """(?is)<%s.*?>.*?</%s>""".format(tag, tag))
  }
  /**
   * Remove single tags, like <br/>, <input ../>
   */
  private def removeSingleTags(tags: List[String])(content: String) = {
    removeTags(tags, content)("""(?is)<[ ]*%s.*?/[ ]*>""".format(_))
  }

  /**
   * Remove *ONLY* tags, not content
   */
  private def removeOnlyTags(tags: List[String])(content: String) = {
    removeTags(tags, content)("""(?is)<[/]?%s.*?>""".format(_))
  }
  /**
   * Simply remove all the patterns in the list
   */
  private def removePatterns(patterns: List[String])(content: String): String = {
    patterns match {
      case Nil => content
      case pattern :: rest => removePatterns(rest)(content.replaceAll(pattern, ""))
    }
  }

  private val pairedTags = List("script", "style")
  private val singleTags = List("link", "input", "br", "img", "meta", "wbr")
  private val uselessTags = List("strong", "em", "font", "b", "p", "li", "ul", "ol", "td", "tr", "th", "tbody", "table")
  private val uselessPatterns = List("""(?is)<!--.*?-->""")
  /**
   * Compose all the removal functions into one
   */
  private val removeAll = List(removeSingleTags(singleTags)_,
    removePairedTags(pairedTags)_,
    removeOnlyTags(uselessTags)_,
    removePatterns(uselessPatterns)_) reduce (_ compose _)
  lazy val strippedLines = removeAll(fullContent) split "\n" map (_.trim) filter (_.length != 0)
  /**
   * The simplified content after removing various tags.
   * Default evaluation strategy is "lazy", avoiding unnecessary overhead.
   */
  lazy val simplifiedContent = strippedLines mkString "\n"
}

/**
 * Class to deal with HTML stored in local disks
 */
class LocalHTMLDocument(val filename: String) extends HTMLDocument with LoggerTrait  {
  val charset = {
    val res = MyCharsetDetector.detectFile(filename).getOrElse(defaultCharset).toLowerCase()
    if (res != "utf8"  && res != "utf-8" && res != defaultCharset)
      defaultCharset
    else
      res
  }
  val url = java.net.URLDecoder.decode(filename, charset)
    /**
   * Get full content, with blank lines stripped
   */
  val allLines = io.Source.fromFile(filename)(charset).getLines
  val fullContent = 
    allLines map (_.trim) filter (_.length != 0) mkString "\n"  
}

/**
 * Class to deal with HTML on the web
 */
class WebHTMLDocument(val html: String) extends HTMLDocument {
	val charset = MyCharsetDetector.detectString(html).getOrElse(defaultCharset)
  val fullContent = html
}

object HTMLDocument extends thu.ailab.global.AppEntry {
  private val fileLoaded = Array[Boolean]()
  val foo = new LocalHTMLDocument("../../Data/blog1000/http%3A%2F%2Fblog.sina.com.cn%2Fs%2Fblog_00f2e45101017icv.html")
  println(foo.simplifiedContent)
}
