
import java.awt.Color

import scala.swing._

class UI extends MainFrame {
    private def restrictHeight(s: Component) {
        s.maximumSize = new Dimension(Short.MaxValue, s.preferredSize.height)
    }

    private def myLabel(string: String, fontSize: Int) = {
        val label = new Label(string) {
            foreground = Color.white
            font = Font.apply(Font.Dialog, Font.Bold, fontSize)
        }
        label
    }
    private def myText = {
        new TextArea {
            foreground = Color.white
            background = Color.darkGray
            font = Font.apply(Font.Dialog, Font.Bold, 12)
        }
    }

    private def myMenu(frame: Frame) = {
        new MenuBar {
            background = Color.darkGray
            foreground = Color.white
            contents += new Menu("Menu") {
                background = Color.darkGray
                foreground = Color.white
                contents += new MenuItem(Action("Quit") { frame.close() }) {
                    background = Color.darkGray
                    foreground = Color.white
                }
            }
        }
    }

    title = "Airport app"
    private val la = new Label("Query or Report ?") {
        foreground = Color.white
        font = Font.apply(Font.Dialog, Font.Bold, 16)
        horizontalAlignment = Alignment.Center
        verticalAlignment = Alignment.Top
    }
    private val query_text: TextArea = new TextArea { rows = 1; lineWrap = true; wordWrap = true; font = Font.apply(Font.Monospaced, Font.Bold, 12)}
    restrictHeight(query_text)
    foreground = Color.darkGray
    background = Color.darkGray
    menuBar = new MenuBar {
        background = Color.darkGray
        foreground = Color.white
        contents += new Menu("Menu") {
            background = Color.darkGray
            foreground = Color.white
            contents += new MenuItem(Action("Quit") { close(); sys.exit(0) }) {
                background = Color.darkGray
                foreground = Color.white
            }
        }
    }
    contents = new BoxPanel(Orientation.Vertical) {
        foreground = Color.yellow.darker()
        background = Color.darkGray
        contents += new BoxPanel(Orientation.Horizontal) {
            background = Color.darkGray
            contents += Swing.HGlue
            contents += la
            contents += Swing.HGlue
        }
        contents += swing.Swing.VStrut(10)
        contents += new BoxPanel(Orientation.Horizontal) {
            private def display_query() = {
                val opt = Dialog.showInput(contents.head, "Enter the name or code of the country you seek information:", initial = "")
                if (opt.isDefined)
                {
                    val mapped_result = DBQueries.query(opt.get)
                    val text = myText
                    text.text = mapped_result.par.foldLeft(StringBuilder.newBuilder)((str, e) => {
                        str ++= "Country: " + e._1 + ":\n"
                        e._2.par.foldLeft(str)((str, e) => str ++= e._1 + ":\n" + e._2.par.mkString("\n") + '\n') + '\n'
                    }).toString
                    text.maximumSize = new Dimension(1280, 720)
                    val window = new Frame {
                        contents = new ScrollPane(text) {
                            preferredSize = new Dimension(480, 720)
                        }
                        menuBar = myMenu(this)
                    }
                    window.visible = true
                }
            }
            private def display_report(): Unit = {
                val (air, lat, runways) = DBQueries.report()
                val top_airports_text = myText
                val top_lat_text = myText
                val runway_types_text = myText
                top_airports_text.text = "top 10 countries with the most airports:\n" + air.take(10).mkString("\n") + '\n'
                top_airports_text.text += "top 10 countries with the less airports:\n" + air.takeRight(10).mkString("\n") + '\n'
                top_lat_text.text = "Top 10 runway latitudes\n" + lat.mkString("\n") + '\n'
                runway_types_text.text = runways.par.foldLeft(StringBuilder.newBuilder)((str, elem) => {
                    str ++= "Runway types for " + elem._1 + ":\n" + elem._2.par.mkString("\n") + '\n'
                }).toString()
                val air_window = new Frame {
                    title = "Top 10 countries with the most/less airports"
                    menuBar = myMenu(this)
                    contents = new ScrollPane(top_airports_text)
                }
                val lat_window = new Frame {
                    title = "Top 10 runway latitutes"
                    menuBar = myMenu(this)
                    contents = new ScrollPane(top_lat_text)
                }
                val run_window = new Frame { title = "runway surfaces by country";
                    menuBar = myMenu(this)
                    contents = new ScrollPane(runway_types_text) { preferredSize = new Dimension(480, 720) }
                }
                air_window.visible = true
                lat_window.visible = true
                run_window.visible = true
            }
            background = Color.darkGray
            contents += new BoxPanel(Orientation.Vertical) {
                background = Color.darkGray
                private val label1 = myLabel("Query all airport informations of a specific country", 14)
                contents += label1
                contents += Swing.VStrut(10)
                private val button = Button("Search") { display_query() }
                button.foreground = Color.black
                button.background = Color.white
                button.horizontalAlignment = Alignment.Center
                contents += new BoxPanel(Orientation.Horizontal) {
                    background = Color.darkGray
                    contents += Swing.HGlue
                    contents += button
                    contents += Swing.HGlue
                }
                preferredSize = new Dimension(label1.preferredSize.width + 10, preferredSize.height)
            }
            contents += Swing.HGlue
            contents += Swing.HStrut(10)
            private val labelor = myLabel("or", 14)
            contents += labelor
            contents += Swing.HGlue
            contents += Swing.HStrut(10)
            contents += new BoxPanel(Orientation.Vertical) {
                background = Color.darkGray
                private val label2 = myLabel("display different statistic reports of the database", 14)
                contents += label2
                contents += Swing.VStrut(10)
                private val button = Button("Report") { display_report() }
                button.foreground = Color.black
                button.background = Color.white
                button.horizontalAlignment = Alignment.Center
                contents += new BoxPanel(Orientation.Horizontal) {
                    background = Color.darkGray
                    contents += Swing.HGlue
                    contents += button
                    contents += Swing.HGlue
                }
                preferredSize = new Dimension(label2.preferredSize.width + 10, preferredSize.height)
            }
        }
        private val quit_button = Button("Quit") { ui.close(); sys.exit(0) }
        quit_button.horizontalAlignment = Alignment.Right
        quit_button.verticalAlignment = Alignment.Bottom
        quit_button.background = Color.white
        quit_button.foreground = Color.black
        contents += Swing.VStrut(20)
        contents += new BoxPanel(Orientation.Horizontal) {
            background = Color.darkGray
            contents += Swing.HGlue
            contents += quit_button
        }
        border = Swing.EmptyBorder(10, 10, 10, 10)
        preferredSize = new Dimension(1280, 720)
    }
    preferredSize = new Dimension(1280, 720)
    val ui = this
    centerOnScreen()

    override def close(): Unit = { super.close(); MyDB.close() }
}
