import scala.io.StdIn
import javax.swing._
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyListener
import java.awt.event.KeyEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

object CounterApplication{
    def main(args: Array[String]): Unit = {
        println("Welcome to the Counter!")
        println(helpmsg)
        println("---------- Have fun counting ----------")
        while(true){
            getInput()
        }
    }

    val helpmsg: String =
    """The following are valid commands:
    exit     - Closes the application
    reset    - Resets the Counter
    c        - Increments the Counter by 1
    add x    - Increments the Counter by x
    counter  - Shows the value of the Counter
    help     - Shows this help message
    gui      - Opens the GUI
    guihelp  - Shows help message for the GUI"""

    val guihelpmsg: String = 
    """The following are valid input for the GUI:
    Space       - Increments the Counter by 1
    Backspace   - Decrements the Counter by 1"""

    def getInput(): Unit = {
        print("> ")
        val rawInput: String = StdIn.readLine()
        val arguments: Array[String] = rawInput.toLowerCase().split(" ")
        val command: String = arguments.apply(0)
        command match {
            case "exit" => println(s"Counter ended at: ${Counter.count}"); System.exit(0)
            case "reset" => Counter.reset(); println(counterStatus())
            case "c" => Counter.increment(); println(counterStatus())
            case "help" => println(helpmsg)
            case "counter" => println(counterStatus())
            case "gui" => Gui.openGUI()
            case "guihelp" => println(guihelpmsg)
            case "add" => {
                try{Counter.add(arguments.apply(1).toInt)}
                catch {case e: Exception => println("Error: nothing added to counter")}
                println(counterStatus())
            }
            case _ => {
                println(s"Sorry, I can't undestand command: $rawInput")
                println("Please try another command, or type \"Help\" for a list of valid commands")
                getInput()
            }
        }
        Gui.updateGUI()
    }

    def counterStatus(): String = {
        s"Counter at: ${Counter.count}"
    }
}

object Gui extends KeyListener{
    val mainframe = new JFrame("CounterGUI")
    val frame = mainframe.getContentPane()
    val counterLabel = new JLabel("<html><h1>Counter</h1></html>", SwingConstants.CENTER)
    val counterText = new JLabel(s"${Counter.count}", SwingConstants.CENTER)
    def openGUI(): Unit = {
        mainframe.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
        mainframe.setFocusable(true)
        mainframe.addKeyListener(this)
        mainframe.addComponentListener(new ComponentAdapter() {
            override def componentResized(x: ComponentEvent): Unit = updateGUI()
        })
        mainframe.requestFocusInWindow()
        mainframe.setAutoRequestFocus(true)
        frame.setLayout(new BorderLayout())
        frame.setBackground(Color.DARK_GRAY)
        frame.add(counterLabel, BorderLayout.NORTH)
        counterLabel.setForeground(Color.WHITE)
        counterText.setForeground(Color.WHITE)
        counterLabel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.MAGENTA))
        frame.add(counterText, BorderLayout.CENTER)
        mainframe.repaint()
        mainframe.pack()
        mainframe.setSize(new Dimension(400, 400))
        mainframe.setVisible(true)
    }
    def updateGUI(): Unit = {
        counterText.setText(s"${Counter.count}")
        val counterTextFont = counterText.getFont()
        val stringWidth: Int = counterText.getFontMetrics(counterTextFont).stringWidth(counterText.getText())
        val compWidth = counterText.getWidth()
        val widthRatio: Double = compWidth.toDouble / stringWidth.toDouble
        val newFontSize: Int = (counterTextFont.getSize() * widthRatio).toInt
        val compHeight = counterText.getHeight()
        val fontSize = Math.min(newFontSize, compHeight)
        counterText.setFont(new Font(counterTextFont.getName(), Font.PLAIN, fontSize))
    }

    def keyPressed(x: KeyEvent): Unit = {x.getKeyCode() match {
            case KeyEvent.VK_SPACE => Counter.increment()
            case KeyEvent.VK_BACK_SPACE => Counter.add(-1)
            case _ => //nothing
        }
        updateGUI()
    }
    def keyReleased(x: KeyEvent): Unit = {} // not implemented
    def keyTyped(x: KeyEvent): Unit = {} // not implemented

}

object Counter{
    var count = 0
    def increment(): Unit = count += 1
    def add(n: Int): Unit = for(k <-1 to n) {increment(); Gui.updateGUI; Thread.sleep(1000/n)}
    def reset(): Unit = count = 0
}