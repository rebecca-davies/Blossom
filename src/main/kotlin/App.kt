import tornadofx.*

fun main() {
    launch<Application>()
}

class Application: App(Window::class)
class Window: View() {
    override val root = vbox {
        button("Press me")
        label("Waiting")
    }
}