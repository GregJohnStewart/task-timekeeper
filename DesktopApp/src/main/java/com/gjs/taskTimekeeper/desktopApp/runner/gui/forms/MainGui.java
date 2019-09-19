package com.gjs.taskTimekeeper.desktopApp.runner.gui.forms;

import com.gjs.taskTimekeeper.backend.Task;
import com.gjs.taskTimekeeper.backend.TimeManager;
import com.gjs.taskTimekeeper.backend.Timespan;
import com.gjs.taskTimekeeper.backend.WorkPeriod;
import com.gjs.taskTimekeeper.backend.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.backend.crudAction.KeeperObjectType;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.ActionDoer;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.PeriodDoer;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.TaskDoer;
import com.gjs.taskTimekeeper.backend.crudAction.actionDoer.TimespanDoer;
import com.gjs.taskTimekeeper.backend.timeParser.TimeParser;
import com.gjs.taskTimekeeper.desktopApp.config.ConfigKeys;
import com.gjs.taskTimekeeper.desktopApp.config.Configuration;
import com.gjs.taskTimekeeper.desktopApp.managerIO.ManagerIO;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.UnEditableTableModel;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.Utils;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener.OpenDialogBoxOnClickListener;
import com.gjs.taskTimekeeper.desktopApp.runner.gui.util.listener.OpenUrlOnClickListener;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.net.URI;
import java.util.List;

/**
 * https://www.jetbrains.com/help/idea/designing-gui-major-steps.html
 */
public class MainGui {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainGui.class);

	private static final String ABOUT_MESSAGE = "Task Timekeeper\n\nVersion: " + Configuration.getProperty(ConfigKeys.APP_VERSION, String.class) +
		                                            "\nUsing Lib version: " + Configuration.getProperty(ConfigKeys.LIB_VERSION, String.class) +
		                                            "\n\nThis program is made for you to easily keep track of time spent on tasks." +
		                                            "\nFor help, please visit the Github for this project." +
		                                            "\nPlease consider donating if you find this program was helpful to you!";

	private static final int INDEX_COL_WIDTH = 35;
	private static final int DATETIME_COL_WIDTH = 130;
	private static final int DURATION_COL_WIDTH = 65;
	private static final DefaultTableCellRenderer CENTER_CELL_RENDERER = new DefaultTableCellRenderer();

	private static final String[] PERIOD_LIST_TABLE_HEADERS = new String[]{"#", "Start", "End", "Duration", "Complete", "Actions"};
	private static final String[] TASK_LIST_TABLE_HEADERS = new String[]{"#", "Name", "Actions"};

	static {
		CENTER_CELL_RENDERER.setHorizontalAlignment(JLabel.CENTER);
	}

	//<editor-fold desc="member variables">
	private TimeManager manager;
	private boolean changed = false;

	private final String origTitle;
	private JFrame mainFrame;

	private JPanel mainPanel;
	private JTabbedPane mainTabPane;
	private JPanel selectedPeriodPanel;
	private JPanel periodsPanel;
	private JPanel tasksPanel;
	private JPanel selectedPeriodBannerPanel;
	private JPanel selectedPeriodSpansPanel;
	private JScrollPane periodsScrollPane;
	private JScrollPane tasksScrollPane;
	private JScrollPane selectedPeriodTaskStatsPane;
	private JButton addPeriodButton;
	private JButton addTaskButton;
	private JScrollPane selectedPeriodSpansPane;
	private JButton selectedPeriodAddSpanButton;

	private JMenuBar mainMenuBar;

	//</editor-fold>

	//<editor-fold desc="constructor and public methods">
	public MainGui(Image icon, String appTitle) {
		LOGGER.info("Starting GUI.");
		this.origTitle = appTitle;

		//setup main frame
		this.mainFrame = new JFrame();
		this.mainFrame.setIconImage(icon);
		this.mainFrame.setContentPane(this.mainPanel);
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//TODO:: tell action handler to not kill process when window closes. not a problem until we do the system tray icon.

		//setup menu bar
		this.mainMenuBar = new JMenuBar();
		//File
		JMenu menu = new JMenu("File");
		JMenuItem menuItem = new JMenuItem("Save (ctrl + s)");
		menu.add(menuItem);
		//TODO:: auto save checkbox
		menuItem = new JMenuItem("Reload from file (ctrl + r)");
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("Close");
		menu.add(menuItem);
		this.mainMenuBar.add(menu);
		//info
		menu = new JMenu("Info");
		menuItem = new JMenuItem("About");
		menuItem.addMouseListener(
			new OpenDialogBoxOnClickListener(
				this.mainFrame,
				ABOUT_MESSAGE,
				"About Task Timekeeper",
				JOptionPane.INFORMATION_MESSAGE,
				icon
			)
		);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("Github");
		menuItem.addMouseListener(
			new OpenUrlOnClickListener(
				URI.create(Configuration.getProperty(ConfigKeys.GITHUB_REPO_URL, String.class))
			)
		);
		menu.add(menuItem);
		menuItem = new JMenuItem("Donate");
		menuItem.addMouseListener(
			new OpenUrlOnClickListener(
				URI.create(Configuration.getProperty(ConfigKeys.DONATE_URL, String.class))
			)
		);
		menu.add(menuItem);
		this.mainMenuBar.add(menu);

		this.mainFrame.setJMenuBar(this.mainMenuBar);
		this.reloadData();

		this.mainFrame.pack();
		this.mainFrame.setVisible(true);

		LOGGER.info("Opened window");
	}

	public boolean stillOpen() {
		return this.mainFrame.isVisible();
	}
	//<editor-fold>

	//<editor-fold desc="Data Loading methods">
	private void reloadData() {
		//read in new manager
		//TODO:: handle errors
		this.manager = ManagerIO.loadTimeManager();
		this.changed = false;

		ActionDoer.resetDoers();
		ActionDoer.setNewestPeriodAsSelectedQuiet(this.manager);

		this.updateUiData();
	}

	private void wasUpdated(boolean wasUpdated) {
		this.changed = this.changed || wasUpdated;
		this.updateUiData();
	}

	private void saveData() {
		//TODO:: handle errors
		ManagerIO.saveTimeManager(this.manager);
		this.changed = false;
		this.updateUiData();
	}

	private void updateUiData() {
		if (this.changed) {
			this.mainFrame.setTitle(this.origTitle + " *");
		} else {
			this.mainFrame.setTitle(this.origTitle);
		}

		//<editor-fold desc="Selected Period Tab">
		{
			WorkPeriod selectedPeriod = ActionDoer.getSelectedWorkPeriod();

			if (selectedPeriod == null) {
				//TODO:: disable selected tab, switch to periods tab
			} else {
				//task details
				{
					Object[][] taskStats = new Object[selectedPeriod.getTasks()
						                                  .size()][];

					int count = 0;
					for (Task task : selectedPeriod.getTasks()) {
						taskStats[count] = new Object[2];

						taskStats[count][0] = task.getName();
						taskStats[count][1] = TimeParser.toDurationString(selectedPeriod.getTotalTimeWith(task));

						count++;
					}

					JTable taskStatsTable = new JTable(new UnEditableTableModel(taskStats, new String[]{"Task Name", "Duration"}));
					taskStatsTable.getColumnModel()
						.getColumn(1)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 1, DURATION_COL_WIDTH);
					this.selectedPeriodTaskStatsPane.setViewportView(taskStatsTable);
				}

				//span details
				{
					Object[][] spanDetails = new Object[selectedPeriod.getNumTimespans()][];

					int count = 0;
					for(Timespan span : new TimespanDoer((PeriodDoer)ActionDoer.getActionDoer(KeeperObjectType.PERIOD)).search(manager, new ActionConfig())){
						spanDetails[count] = new Object[5];

						spanDetails[count][0] = count++;
						spanDetails[count][1] = TimeParser.toOutputString(span.getStartTime());
						spanDetails[count][2] = TimeParser.toOutputString(span.getEndTime());
						spanDetails[count][3] = TimeParser.toDurationString(span.getDuration());
						spanDetails[count][4] = span.getTask().getName();
						spanDetails[count][5] = new JButton("button 1");

						count++;
					}
					JTable taskStatsTable = new JTable(new UnEditableTableModel(spanDetails, new String[]{"#", "Start", "End", "Duration", "Task", "Actions"}));
					taskStatsTable.getColumnModel()
						.getColumn(0)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 0, INDEX_COL_WIDTH);
					taskStatsTable.getColumnModel()
						.getColumn(1)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 1, DATETIME_COL_WIDTH);
					taskStatsTable.getColumnModel()
						.getColumn(2)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 2, DATETIME_COL_WIDTH);
					taskStatsTable.getColumnModel()
						.getColumn(3)
						.setCellRenderer(CENTER_CELL_RENDERER);
					Utils.setColWidth(taskStatsTable, 3, DURATION_COL_WIDTH);
					this.selectedPeriodSpansPane.setViewportView(taskStatsTable);
				}
			}
		}
		//</editor-fold>

		//<editor-fold desc="Periods tab">
		{
			List<WorkPeriod> periods = new PeriodDoer().search(this.manager, new ActionConfig());
			Object[][] periodData = new Object[periods.size()][];

			int curInd = 0;
			for (WorkPeriod period : periods) {
				periodData[curInd] = new Object[PERIOD_LIST_TABLE_HEADERS.length];

				periodData[curInd][0] = curInd + 1;
				periodData[curInd][1] = TimeParser.toOutputString(period.getStart());
				periodData[curInd][2] = TimeParser.toOutputString(period.getEnd());
				periodData[curInd][3] = TimeParser.toDurationString(period.getTotalTime());
				periodData[curInd][4] = (period.isUnCompleted() ? "No" : "Yes");
				periodData[curInd][5] = new JButton("action1");//TODO:: figure out these buttons in a table cell.
				curInd++;
			}

			JTable periodListTable = new JTable(new UnEditableTableModel(periodData, PERIOD_LIST_TABLE_HEADERS));
			periodListTable.getColumnModel()
				.getColumn(0)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 0, INDEX_COL_WIDTH);
			periodListTable.getColumnModel()
				.getColumn(1)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 1, DATETIME_COL_WIDTH);
			periodListTable.getColumnModel()
				.getColumn(2)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 2, DATETIME_COL_WIDTH);
			periodListTable.getColumnModel()
				.getColumn(3)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 3, DURATION_COL_WIDTH);
			periodListTable.getColumnModel()
				.getColumn(4)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(periodListTable, 4, 85);
			this.periodsScrollPane.setViewportView(periodListTable);
		}
		//</editor-fold>

		//<editor-fold desc="Tasks tab">
		{
			List<Task> tasks = new TaskDoer().search(this.manager, new ActionConfig());
			Object[][] periodData = new Object[tasks.size()][];

			int curInd = 0;
			for (Task task : tasks) {
				periodData[curInd] = new Object[TASK_LIST_TABLE_HEADERS.length];

				periodData[curInd][0] = curInd + 1;
				periodData[curInd][1] = task.getName();
				periodData[curInd][2] = new JButton("action1");//TODO:: figure out these buttons in a table cell.
				curInd++;
			}

			JTable tabListTable = new JTable(new UnEditableTableModel(periodData, TASK_LIST_TABLE_HEADERS));
			tabListTable.getColumnModel()
				.getColumn(0)
				.setCellRenderer(CENTER_CELL_RENDERER);
			Utils.setColWidth(tabListTable, 0, INDEX_COL_WIDTH);

			this.tasksScrollPane.setViewportView(tabListTable);
		}
		//</editor-fold>

		//TODO:: rest of this
	}
	//</editor-fold>


	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainPanel.setMinimumSize(new Dimension(650, 500));
		mainPanel.setPreferredSize(new Dimension(650, 500));
		mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
		mainTabPane = new JTabbedPane();
		mainPanel.add(mainTabPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
		selectedPeriodPanel = new JPanel();
		selectedPeriodPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Selected Period", selectedPeriodPanel);
		selectedPeriodBannerPanel = new JPanel();
		selectedPeriodBannerPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodPanel.add(selectedPeriodBannerPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 100), new Dimension(-1, 100), new Dimension(-1, 100), 0, false));
		selectedPeriodBannerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Period Details"));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		selectedPeriodSpansPanel = new JPanel();
		selectedPeriodSpansPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		panel1.add(selectedPeriodSpansPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		selectedPeriodSpansPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Spans"));
		selectedPeriodSpansPane = new JScrollPane();
		selectedPeriodSpansPane.setVerticalScrollBarPolicy(22);
		selectedPeriodSpansPanel.add(selectedPeriodSpansPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		selectedPeriodSpansPanel.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		selectedPeriodAddSpanButton = new JButton();
		selectedPeriodAddSpanButton.setText("Add Span");
		panel2.add(selectedPeriodAddSpanButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		selectedPeriodTaskStatsPane = new JScrollPane();
		selectedPeriodTaskStatsPane.setVerticalScrollBarPolicy(22);
		panel1.add(selectedPeriodTaskStatsPane, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(250, -1), new Dimension(250, -1), new Dimension(250, -1), 0, false));
		selectedPeriodTaskStatsPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Task Stats"));
		periodsPanel = new JPanel();
		periodsPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Periods", periodsPanel);
		periodsScrollPane = new JScrollPane();
		periodsScrollPane.setVerticalScrollBarPolicy(22);
		periodsPanel.add(periodsScrollPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		periodsPanel.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		addPeriodButton = new JButton();
		addPeriodButton.setText("Add Period");
		panel3.add(addPeriodButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		tasksPanel = new JPanel();
		tasksPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainTabPane.addTab("Tasks", tasksPanel);
		tasksScrollPane = new JScrollPane();
		tasksScrollPane.setVerticalScrollBarPolicy(22);
		tasksPanel.add(tasksScrollPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		final JPanel panel4 = new JPanel();
		panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		tasksPanel.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		addTaskButton = new JButton();
		addTaskButton.setText("Add Task");
		panel4.add(addTaskButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}

}
