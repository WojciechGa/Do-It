package com.wojciech;

import com.sun.javafx.property.adapter.PropertyDescriptor;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.beans.property.ReadOnlyIntegerProperty.readOnlyIntegerProperty;

public class Controller implements Initializable {

    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    int lastTaskMapID = 0;
    private final Task currentTask = new Task();
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

   @FXML
   private TableView<Task> taskTable;
        @FXML private TableColumn<Task, String> priorityColumn;
        @FXML private TableColumn<Task, String> descriptionColumn;
        @FXML private TableColumn<Task, String> progressColumn;
    @FXML private Button addButton;
    @FXML private TextField description;
    @FXML private ProgressBar progressBar = new ProgressBar(0);
    @FXML private ComboBox<String> comboPrioryties;
    @FXML private Spinner<Integer> progressSpinner;
    @FXML private CheckBox complitedCheckBox;
    @FXML private Button deleteButton;

    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }
    public void addButtonOn(ActionEvent actionEvent) {
        if(currentTask.getId() != null) {
           Task t = taskMap.get(currentTask.getId());
           t.setPriority(currentTask.getPriority());
           t.setDescription(currentTask.getDescription());
           t.setProgress(currentTask.getProgress());
        }else{
            Task t = new Task(++lastTaskMapID, comboPrioryties.getValue(), description.getText(), progressSpinner.getValue());
            tasks.add(t);
            taskMap.put(lastTaskMapID, t);
        }
        setCurrentTaks(null);
    }

    public void cancelOn(ActionEvent actionEvent) {

        setCurrentTaks(null);
        taskTable.getSelectionModel().clearSelection();
    }

    public void completOn(ActionEvent actionEvent) {
        progressSpinner.getValueFactory().setValue(100);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //setting prioritiesbox values
        comboPrioryties.getItems().addAll("High", "Medium", "Low");

        //  setting progress spinner
        progressSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,100,0));

        //checbox to true if progress is 100%
        progressSpinner.valueProperty().addListener((ObservableValue<? extends Integer> observable,Integer oldValue,Integer newValue) ->{

            if(newValue == 100){
                complitedCheckBox.setSelected(true);
            }else {
                complitedCheckBox.setSelected(false);
            }
            progressBar.setProgress(1.0 * newValue / 100);
        } );

        //ustawianie tabeli
        taskTable.setItems(tasks);
        priorityColumn.setCellValueFactory(rowData -> rowData.getValue().priorityProperty());
        descriptionColumn.setCellValueFactory(rowData -> rowData.getValue().descriptionProperty());
        progressColumn.setCellValueFactory(rowData -> Bindings.concat(rowData.getValue().progressProperty(),"%"));

        comboPrioryties.valueProperty().bindBidirectional(currentTask.priorityProperty());
        description.textProperty().bindBidirectional(currentTask.descriptionProperty());
        progressSpinner.getValueFactory().valueProperty().bindBidirectional(currentTask.progressProperty());

        addButton.disableProperty().setValue(true);
        deleteButton.disableProperty().setValue(true);

        description.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() < 4){
                    addButton.disableProperty().setValue(true);
                }else{
                    addButton.disableProperty().setValue(false);
                }
            }
        });

        StringBinding addButtonTextBinding = new StringBinding() {
            {
                super.bind(currentTask.idProperty());
            }
            @Override
            protected String computeValue() {
                if(currentTask.getId() == null)
                    return "Add";
                else
                    return "Update";
            }
        };
        addButton.textProperty().bind(addButtonTextBinding);
        taskTable.getSelectionModel().selectedItemProperty().addListener(((ObservableValue<? extends Task> observable,Task oldValue,Task newValue) -> {
            setCurrentTaks(newValue);
            if(newValue != null)
                deleteButton.disableProperty().setValue(false);
            else
                deleteButton.disableProperty().setValue(true);
        }));


    }

    private void setCurrentTaks(Task selectedTask) {
        if(selectedTask != null){
            currentTask.setId(selectedTask.getId());
            currentTask.setPriority(selectedTask.getPriority());
            currentTask.setDescription(selectedTask.getDescription());
            currentTask.setProgress(selectedTask.getProgress());
        }else{
            currentTask.setId(null);
            currentTask.setPriority("");
            currentTask.setDescription("");
            currentTask.setProgress(0);
        }
    }

    public void setTasksMap(HashMap<Integer,Task> initialTaskMap){
        taskMap.clear();
        tasks.clear();
        taskMap.putAll(initialTaskMap);
        tasks.addAll(taskMap.values());
        if(!initialTaskMap.isEmpty())
            lastTaskMapID = taskMap.keySet().stream().max(Integer::compare).get();
    }

    public void deleteButtonOn(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting task...");
        alert.setHeaderText("Do you wan to delete task?");
        alert.getButtonTypes().remove(0,2);
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get() == ButtonType.YES) {
            taskMap.remove(currentTask.getId(), taskMap.get(currentTask.getId()));
            taskTable.getSelectionModel().clearSelection();
            setCurrentTaks(null);
            tasks.clear();
            tasks.addAll(taskMap.values());
        }
    }
}
