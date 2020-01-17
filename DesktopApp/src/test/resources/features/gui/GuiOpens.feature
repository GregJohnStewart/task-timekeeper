Feature: Gui is capable of opening.

  This tests that the Gui can be opened with any amount of valid data.

  Scenario Outline: The Gui is opened
    Given I use the <DataSet> data set.
    When the gui is opened
    Then the gui will open without failing

    Examples:
    | DataSet |
    | empty   |
    | full    |
