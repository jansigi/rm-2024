package ch.js.rm2024.view

import ch.js.rm2024.repository.*
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.table.DefaultTableModel

import java.awt.FlowLayout

class AnalysisWindow(private val analysisDTO: AnalysisDTO) : JFrame("Analysis - ${analysisDTO.title}") {
    private val criteriaTableModel = DefaultTableModel(arrayOf("Criterion", "%"), 0)
    private val variantsTableModel = DefaultTableModel(emptyArray(), 0)
    private val criteriaTable = JTable(criteriaTableModel)
    private val variantsTable = JTable(variantsTableModel)

    init {
        defaultCloseOperation = DISPOSE_ON_CLOSE
        layout = BorderLayout()

        // Criteria Panel
        val criteriaPanel = JPanel(BorderLayout())
        criteriaTable.autoResizeMode = JTable.AUTO_RESIZE_OFF
        criteriaPanel.add(JScrollPane(criteriaTable), BorderLayout.CENTER)
        add(criteriaPanel, BorderLayout.WEST)

        // Variants Panel
        val variantsPanel = JPanel(BorderLayout())
        variantsTable.autoResizeMode = JTable.AUTO_RESIZE_OFF
        variantsPanel.add(JScrollPane(variantsTable), BorderLayout.CENTER)
        add(variantsPanel, BorderLayout.CENTER)

        // Criteria Button Panel
        val criteriaButtonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val createCriterionButton = JButton("Create Criterion")
        createCriterionButton.addActionListener { createCriterion() }
        criteriaButtonPanel.add(createCriterionButton)
        add(criteriaButtonPanel, BorderLayout.NORTH)

        // Variants Button Panel
        val variantsButtonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val addVariantButton = JButton("Add Variant")
        addVariantButton.addActionListener { addVariant() }
        variantsButtonPanel.add(addVariantButton)
        add(variantsButtonPanel, BorderLayout.SOUTH)

        pack()
        isVisible = true
    }

    private fun createCriterion() {
        val name = JOptionPane.showInputDialog(
            this,
            "Enter name for the new criterion:",
            "Create Criterion",
            JOptionPane.PLAIN_MESSAGE
        )?.trim()
        val prozent = JOptionPane.showInputDialog(
            this,
            "Enter prozent for the new criterion:",
            "Create Criterion",
            JOptionPane.PLAIN_MESSAGE
        )?.trim()

        if (!name.isNullOrEmpty() && prozent != null && prozent.toDoubleOrNull() != null) {
            val newCriterion = Criterion(name = name, weight = prozent.toDouble())
            analysisDTO.criteria.add(newCriterion)
            criteriaTableModel.addRow(arrayOf(name, "$prozent%"))
        }
    }

    private fun addVariant() {
        val name = JOptionPane.showInputDialog(
            this,
            "Enter Name for the new variant:",
            "Add Variant",
            JOptionPane.PLAIN_MESSAGE
        )?.trim()

        if (!name.isNullOrEmpty()) {
            val criteriaValues = mutableListOf<String?>()
            analysisDTO.criteria.forEach { criterion ->
                val value = JOptionPane.showInputDialog(
                    this,
                    "Enter ${criterion.name} for the new variant:",
                    "Add Variant",
                    JOptionPane.PLAIN_MESSAGE
                )?.trim()
                criteriaValues.add(value)
            }

            if (criteriaValues.none { it.isNullOrEmpty() }) {
                val newVariant = Variant(name = name, criteria = criteriaValues.filterNotNull())
                analysisDTO.variants.add(newVariant)

                val columnCount = variantsTableModel.columnCount
                // Add a new column for the variant
                variantsTableModel.addColumn(name)

                // Ensure the table has enough rows
                val rowCount = variantsTableModel.rowCount
                if (rowCount < analysisDTO.criteria.size + 1) {
                    variantsTableModel.rowCount = analysisDTO.criteria.size + 1
                }

                // Set criteria values as rows under the new column
                criteriaValues.forEachIndexed { index, value ->
                    variantsTableModel.setValueAt(value, index, columnCount)
                }
            }
        }
    }
}
