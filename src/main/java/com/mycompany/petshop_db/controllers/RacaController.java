package com.mycompany.petshop_db.controllers;

import com.mycompany.petshop_db.dao.DatabaseRaca;
import com.mycompany.petshop_db.models.raca;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class RacaController implements Initializable {

    private Menu activeMenu;
    private String situacao = "";

    @FXML
    private MenuBar menuBar;
    @FXML
    private TableView<raca> data;
    @FXML
    private TableColumn<raca, Integer> idtable;
    @FXML
    private TableColumn<raca, String> descricaotable;
    @FXML
    private TextField descricao;
    @FXML
    private Button adicionar;
    @FXML
    private Button excluir;
    @FXML
    private Button salvar;
    @FXML
    private Button editar;
    @FXML
    private TextField pesquisar;
    @FXML
    private Button btnpesquisar;
    @FXML
    private Button btnlimpar;

    private List<raca> racasAdicionadas = new ArrayList<>();
    private List<raca> racasExcluidas = new ArrayList<>();
    private List<raca> racasEditadas = new ArrayList<>();

    private ChangeListener<String> descricaoListener;

    @FXML
    private void handleAdicionarOrcamento(ActionEvent event) {
        try {
            loadPage("/fxml/TelaPrincipal.fxml", "Adicionar Orçamento");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVisualizarOrcamento(ActionEvent event) {
        try {
            loadPage("/fxml/ExibirOrcamento.fxml", "Visualizar Orçamento");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClientes(ActionEvent event) throws IOException {
        loadPage("/fxml/Clientes.fxml", "Clientes");
    }

    @FXML
    private void handlePet(ActionEvent event) throws IOException {
        loadPage("/fxml/Pet.fxml", "Pet");
    }

    @FXML
    private void handleProdutos(ActionEvent event) throws IOException {
        loadPage("/fxml/Produtos.fxml", "Produtos");
    }

    @FXML
    private void handleVeterinarios(ActionEvent event) throws IOException {
        loadPage("/fxml/Veterinarios.fxml", "Veterinarios");
    }

    @FXML
    private void handleRacas(ActionEvent event) throws IOException {
        loadPage("/fxml/Racas.fxml", "Raças");
    }

    @FXML
    private void handleAtendentes(ActionEvent event) throws IOException {
        loadPage("/fxml/Atendentes.fxml", "Atendentes");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Menu defaultMenu = menuBar.getMenus().get(5);
        setActiveMenu(defaultMenu);

        idtable.setCellValueFactory(new PropertyValueFactory<>("id"));
        descricaotable.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        data.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            removeListeners();
            if (newSelection != null) {
                handleSelecionarItem();
            }
        });
        loadRacasData();
    }

    private void loadPage(String page, String menuText) throws IOException {
        Stage currentStage = (Stage) data.getScene().getWindow();
        currentStage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle(menuText);
        newStage.show();
    }

    private void loadRacasData() {
        racasAdicionadas.clear();
        racasExcluidas.clear();
        racasEditadas.clear();
        data.getSelectionModel().clearSelection();
        limparCampos();
        removeListeners();

        List<raca> racasList = DatabaseRaca.getRacas();
        ObservableList<raca> observableRacasList = FXCollections.observableArrayList(racasList);
        data.setItems(observableRacasList);
    }

    private void setFieldsDisabled(boolean disabled) {
        descricao.setDisable(disabled);
    }

    @FXML
    private void handleAdicionar(ActionEvent event) {
        if (descricao.getText().isEmpty()) {
            mostrarAlerta("Descrição da raça não pode estar vazia.");
            return;
        }

        int novoId = determinarNovoId();

        raca newRaca = new raca();
        newRaca.setId(novoId);
        newRaca.setDescricao(descricao.getText());

        data.getItems().add(newRaca);
        racasAdicionadas.add(newRaca);
        situacao = "a";
    }

    @FXML
    private void handleExcluir(ActionEvent event) {
        removeListeners();
        ObservableList<raca> selectedItems = data.getSelectionModel().getSelectedItems();
        if (!racasAdicionadas.isEmpty()) {
            mostrarAlerta("Salve o conteúdo antes de excluir.");
            return;
        }
        situacao = "e";

        for (raca raca : selectedItems) {
            racasAdicionadas.remove(raca);
            racasExcluidas.add(raca);
        }
        data.getItems().removeAll(selectedItems);
    }

    @FXML
    private void handleEditar(ActionEvent event) {
        if (!racasAdicionadas.isEmpty()) {
           mostrarAlerta("Salve o conteúdo antes de Editar");
           return; 
        }
        
        raca selectedRaca = data.getSelectionModel().getSelectedItem();
        if (selectedRaca != null) {
            setFieldsDisabled(false);
            situacao = "edt";
            addListeners(selectedRaca);
        }
    }

    @FXML
    private void handleSalvar() {
        removeListeners();

        switch (situacao) {
            case "a":
                DatabaseRaca.salvarRaca(racasAdicionadas);
                mostrarAlerta("Raça(s) adicionados com sucesso!");
                break;
            case "e":
                if (!racasEditadas.isEmpty()) {
                    for (raca raca : racasEditadas) {
                        DatabaseRaca.editarRaca(raca);
                    }
                }
                for (raca raca : racasExcluidas) {
                    DatabaseRaca.excluirRaca(raca.getId());
                }
                mostrarAlerta("Raça(s) excluidos com sucesso!");
                break;
            case "edt":
                if (!racasExcluidas.isEmpty()) {    
                    for (raca raca : racasExcluidas) {
                        DatabaseRaca.excluirRaca(raca.getId());
                    }
                }
                for (raca raca : racasEditadas) {
                    DatabaseRaca.editarRaca(raca);
                }
                mostrarAlerta("Raça(s) editadas com sucesso!");
                break;
            default:
                break;
        }
        racasAdicionadas.clear();
        racasExcluidas.clear();
        racasEditadas.clear();
        loadRacasData();
    }

    @FXML
    private void handlePesquisar(ActionEvent event) {
        removeListeners();

        String descricaoSearch = descricao.getText();

        List<raca> resultados = DatabaseRaca.procurarRaca(descricaoSearch);
        ObservableList<raca> observableResultados = FXCollections.observableArrayList(resultados);
        data.setItems(observableResultados);
    }

    @FXML
    private void handleLimpar(ActionEvent event) {
        removeListeners();
        limparCampos();
        setFieldsDisabled(false);
        loadRacasData();
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        racasAdicionadas.clear();
        racasExcluidas.clear();
        racasEditadas.clear();
        loadRacasData();
    }

    @FXML
    private void handleSelecionarItem() {
        raca selectedRaca = data.getSelectionModel().getSelectedItem();
        if (selectedRaca != null) {
            descricao.setText(selectedRaca.getDescricao());
            setFieldsDisabled(true);
        }
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void setActiveMenu(Menu menu) {
        if (activeMenu != null) {
            activeMenu.setStyle("");
        }
        activeMenu = menu;
        activeMenu.setStyle("-fx-background-color: lightblue;");
    }

    private int determinarNovoId() {
        if (data.getItems().isEmpty()) {
            return 1;
        } else {
            raca ultimaRaca = data.getItems().get(data.getItems().size() - 1);
            return ultimaRaca.getId() + 1;
        }
    }

    private void limparCampos() {
        removeListeners();
        descricao.clear();
    }

    private void addListeners(raca selectedRaca) {
        descricao.textProperty().addListener(descricaoListener = (obs, oldValue, newValue) -> {
            selectedRaca.setDescricao(newValue);
            updateTableData();
            updateEditList(selectedRaca);
        });
    }

    private void updateEditList(raca editedRaca) {
        if (!racasEditadas.contains(editedRaca)) {
            racasEditadas.add(editedRaca);
        }
    }

    private void updateTableData() {
        data.refresh();
    }

    private void removeListeners() {
        if (descricaoListener != null) {
            descricao.textProperty().removeListener(descricaoListener);
        }
    }
}
