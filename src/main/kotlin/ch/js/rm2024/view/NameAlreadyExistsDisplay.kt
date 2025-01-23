package ch.js.rm2024.view

import javax.swing.JFrame
import javax.swing.JOptionPane

class NameAlreadyExistsDisplay(parent: JFrame, entity: String) {
    init {
        JOptionPane.showMessageDialog(
            parent,
            "$entity with this title already exists. Please choose a unique title.",
            "Not Unique",
            JOptionPane.WARNING_MESSAGE
        )
    }
}