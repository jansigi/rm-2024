package ch.js.rm2024.view

import ch.js.rm2024.repository.AnalysisDTO
import ch.js.rm2024.service.Service
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class MainWindow : JFrame("Prioritize") {
    private val analysisListModel = DefaultListModel<String>()
    private val analysisList = JList(analysisListModel)

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(800, 600)
        minimumSize = Dimension(600, 400)

        layout = BorderLayout()
        add(JScrollPane(analysisList), BorderLayout.CENTER)

        val buttonPanel = JPanel()
        val createButton = JButton("Create")
        val renameButton = JButton("Rename")
        val deleteButton = JButton("Delete")

        // Button listeners would go here
        createButton.addActionListener { onCreate() }
        renameButton.addActionListener { onRename() }
        deleteButton.addActionListener { onDelete() }

        buttonPanel.add(createButton)
        buttonPanel.add(renameButton)
        buttonPanel.add(deleteButton)

        add(buttonPanel, BorderLayout.SOUTH)

        analysisList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && analysisList.selectedValue != null) {
                    val selectedAnalysisIndex = analysisList.locationToIndex(e.point)
                    val selectedAnalysis = Service.AnalysisRepository.getAll()[selectedAnalysisIndex]
                    openAnalysisWindow(selectedAnalysis)
                }
            }
        })

        loadAnalyses()
    }

    private fun onCreate() {
        // Show input dialog to get the title for the new analysis
        val title = JOptionPane.showInputDialog(
            this,
            "Enter title for the new analysis:",
            "Create Analysis",
            JOptionPane.PLAIN_MESSAGE
        )?.trim()

        // Proceed only if the title is not null and not empty
        if (!title.isNullOrEmpty()) {
            // Check if the title already exists
            if (Service.AnalysisRepository.find { it.title == title } != null) {
                // Inform the user that the title must be unique
                NameAlreadyExistsDisplay(this, "An Analysis")
            } else {
                try {
                    // Create the new Analysis entity
                    Service.AnalysisRepository.new(title)

                    // Refresh the analyses list to include the new analysis
                    loadAnalyses()
                } catch (e: Exception) {
                    // Handle any exceptions
                    JOptionPane.showMessageDialog(
                        this,
                        "Error creating analysis: ${e.message}",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }
    }

    private fun onRename() {
        // Check if analysis is selected
        val selectedAnalysisIndex = analysisList.selectedIndex
        if (selectedAnalysisIndex == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an analysis to rename.",
                "No Analysis Selected",
                JOptionPane.WARNING_MESSAGE
            )
            return
        }

        // get selected analysis
        val selectedAnalysis = Service.AnalysisRepository.getAll()[selectedAnalysisIndex]

        // Show input dialog to get the new title for the selected analysis
        val newTitle = JOptionPane.showInputDialog(this, "Enter new title for the analysis:", selectedAnalysis.title)

        // check if the user entered a title and pressed OK
        if (newTitle != null && newTitle.trim().isNotEmpty()) {
            // Check for title uniqueness
            val isUnique = Service.AnalysisRepository.getAll()
                .none { it.title.equals(newTitle.trim(), ignoreCase = true) && it.id != selectedAnalysis.id }
            if (isUnique) {
                try {
                    // Update the analysis title
                    Service.AnalysisRepository.updateTitle(selectedAnalysis.id, newTitle.trim())

                    // Refresh the analyses list to reflect the new title
                    loadAnalyses()

                    // Optionally, reselect the renamed analysis
                    analysisListModel.elements().toList().indexOf(newTitle.trim()).takeIf { it >= 0 }?.let { index ->
                        analysisList.selectedIndex = index
                    }
                } catch (e: Exception) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Error renaming analysis: ${e.message}",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            } else {
                NameAlreadyExistsDisplay(this, "An Analysis")
            }
        }
        // If the user is cancelled the dialog, do nothing
    }

    private fun onDelete() {
        // Check if an analysis is selected
        val selectedAnalysisIndex = analysisList.selectedIndex
        if (selectedAnalysisIndex == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an analysis to delete.",
                "No Analysis Selected",
                JOptionPane.WARNING_MESSAGE
            )
            return
        }

        // Get the selected analysis
        val selectedAnalysis = Service.AnalysisRepository.getAll()[selectedAnalysisIndex]

        val confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the analysis: \"${selectedAnalysis.title}\"?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        )

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Service.AnalysisRepository.remove(selectedAnalysis.id)
                loadAnalyses()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error deleting analysis: ${e.message}",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
        // if not confirmed -> do nothing
    }

    private fun openAnalysisWindow(selectedAnalysisDTO: AnalysisDTO) {
        val analysisWindow = AnalysisWindow(selectedAnalysisDTO)
        analysisWindow.isVisible = true
    }

    private fun loadAnalyses() {
        analysisListModel.clear()

        val analyses = Service.AnalysisRepository.getAll()

        analyses.forEach { analysis ->
            analysisListModel.addElement(analysis.title)
        }
    }
}