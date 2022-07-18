import com.zegreatrob.coupling.model.player.Player
import kotlinx.browser.document
import org.w3c.dom.Document
import org.w3c.dom.get
import react.FC
import react.PropsWithChildren
import react.create
import react.dom.client.createRoot

val player = Player()

fun main() {
    createRoot(document.viewContainerNode)
        .render(RootComponent.create { +"Content: $player" })
}

private val Document.viewContainerNode get() = getElementsByClassName("view-container")[0]!!

val RootComponent = FC<PropsWithChildren> { props ->
    +"Root"
    +props.children
}
