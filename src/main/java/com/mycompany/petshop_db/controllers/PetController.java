package com.mycompany.petshop_db.controllers;

import com.mycompany.petshop_db.dao.DatabasePet;
import com.mycompany.petshop_db.models.pet;
import com.mycompany.petshop_db.models.pet.Sexo;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class PetController implements Initializable {
    
    private Menu activeMenu;
    private String situacao = "";

    @FXML
    private MenuBar menuBar;
    @FXML
    private TableView<pet> data;
    @FXML
    private TableColumn<pet, Integer> idtable;
    @FXML
    private TableColumn<pet, String> nometable;
    @FXML
    private TableColumn<pet, String> sexotable;
    @FXML
    private TableColumn<pet, String> cpftable;
    @FXML
    private TableColumn<pet, String> racatable;
    @FXML
    private TextField raca;
    @FXML
    private ComboBox<String> sexo;
    @FXML
    private Button adicionar;
    @FXML
    private Button excluir;
    @FXML
    private Button salvar;
    @FXML
    private TextField nome;
    @FXML
    private TextField cpf;
    @FXML
    private TextField pesquisar;
    @FXML
    private Button btnpesquisar;
    @FXML
    private Button btnlimpar;
    
    
    private List<pet> petsAdicionados = new ArrayList<>();
    private List<pet> petsExcluidos = new ArrayList<>();
    private List<pet> petsEditados = new ArrayList<>();
    

    private ChangeListener<String> nomeListener;
    private ChangeListener<String> sexoListener;
    private ChangeListener<String> cpfListener;
    private ChangeListener<String> racaListener;
  
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
        Menu defaultMenu = menuBar.getMenus().get(2);
        setActiveMenu(defaultMenu);
        
        idtable.setCellValueFactory(new PropertyValueFactory<>("id"));
        nometable.setCellValueFactory(new PropertyValueFactory<>("nome"));
        sexotable.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        cpftable.setCellValueFactory(new PropertyValueFactory<>("clienteId"));
        racatable.setCellValueFactory(new PropertyValueFactory<>("racaId"));
        
        ObservableList<String> sexOptions = FXCollections.observableArrayList("MACHO", "FÊMEA");
        sexo.setItems(sexOptions);
        
        loadPetsData();
        
        data.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        removeListeners();
            if (newSelection != null) {
                handleSelecionarItem();
            }
        });
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
    
    private void loadPetsData() {
        petsAdicionados.clear();
        petsExcluidos.clear();
        petsEditados.clear();
        data.getSelectionModel().clearSelection();
        limparCampos();
        removeListeners();
             
        List<pet> petsList = DatabasePet.getPets();
        ObservableList<pet> observablePetsList = FXCollections.observableArrayList(petsList);
        data.setItems(observablePetsList);
    }
    private void setFieldsDisabled(boolean disabled) {
        nome.setDisable(disabled);
        sexo.setDisable(disabled);
        cpf.setDisable(disabled);
        raca.setDisable(disabled);
    }
 
    @FXML
    private void handleAdicionar(ActionEvent event) {
        String cpfText = cpf.getText();

        if (Validation.isAnyFieldEmpty(
                cpf.getText(),
                raca.getText(),
                sexo.getValue(),
                nome.getText()
            )) {
            return;
        }

        if (!Validation.isNumeroInteiro(raca.getText(), "Raça")) {
            return;
        }

        if (!Validation.isCPFValido(cpf.getText())) {
            return;
        }

        int racaNum = Integer.parseInt(raca.getText());

        Sexo sexoEnum;
        if (sexo.getValue().equals("MACHO")) {
            sexoEnum = Sexo.MACHO;
        } else if (sexo.getValue().equals("FÊMEA")) {
            sexoEnum = Sexo.FEMEA;
        } else {
            return;
        }
        int novoId = determinarNovoId();
        pet newPet = new pet(cpfText,nome.getText(),sexoEnum,racaNum);
        newPet.setId(novoId);

        data.getItems().add(newPet);
        petsAdicionados.add(newPet);
        situacao = "a";

    }
    @FXML
    private void handleExcluir(ActionEvent event) {
        removeListeners();
        ObservableList<pet> selectedItems = data.getSelectionModel().getSelectedItems();
        if (!petsAdicionados.isEmpty()) {
            mostrarAlerta("Salve o conteúdo antes de excluir.");
            return;
        }

        situacao = "e";

        for (pet pet : selectedItems) {
            petsAdicionados.remove(pet);
            petsExcluidos.add(pet);
        }
        data.getItems().removeAll(selectedItems);
    }

    @FXML
    private void handleEditar(ActionEvent event) {

        pet selectedPet = data.getSelectionModel().getSelectedItem();
        if (!petsAdicionados.isEmpty()) {
           mostrarAlerta("Salve o conteúdo antes de Editar");
           return; 
        }
       
        if (selectedPet != null) {
            setFieldsDisabled(false);
            situacao = "edt";
            addListeners(selectedPet);
        }
    }

    @FXML
    private void handleSalvar() {
        removeListeners();

        switch (situacao) {
            case "a":
                DatabasePet.salvarPet(petsAdicionados);
                mostrarAlerta("Pet(s) adicionados com sucesso!");
                break;
            case "e":
                if (!petsEditados.isEmpty()) {
                    for (pet pet : petsEditados) {
                        DatabasePet.editarPet(pet);
                    }
                }
                for (pet pet : petsExcluidos) {
                    DatabasePet.excluirPet(pet);
                }
                mostrarAlerta("Pet(s) excluidos com sucesso!");
                break;
            case "edt":
                if (!petsExcluidos.isEmpty()) {
                    for (pet pet : petsExcluidos) {
                        DatabasePet.excluirPet(pet);
                    }
                }
                for (pet pet : petsEditados) {
                    if (!Validation.isCPFValido(pet.getClienteId())) {
                        return;
                    }

                    if (!Validation.isNumeroInteiro(String.valueOf(pet.getRacaId()), "Número")) {
                        return;
                    }
                    DatabasePet.editarPet(pet);
                }
                mostrarAlerta("Pet(s) editados com sucesso!");
                break;
            default:
                break;
        }
        petsAdicionados.clear();
        petsExcluidos.clear();
        petsEditados.clear();
        loadPetsData();
    }

    @FXML
    private void handlePesquisar(ActionEvent event) {
        removeListeners();

        String nomeSearch = nome.getText();
        String cpfSearch = cpf.getText();
        String racaSearch = raca.getText();
        String sexoSearch = sexo.getValue();

        List<pet> resultados = DatabasePet.procurarPet(nomeSearch, cpfSearch, racaSearch, sexoSearch);
        ObservableList<pet> observableResultados = FXCollections.observableArrayList(resultados);
        data.setItems(observableResultados);
    }

    @FXML
    private void handleLimpar(ActionEvent event) {
        removeListeners();
        limparCampos();
        setFieldsDisabled(false);
        loadPetsData();
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        petsAdicionados.clear();
        petsExcluidos.clear();
        petsEditados.clear();
        loadPetsData();
    }

    @FXML
    private void handleSelecionarItem() {
        pet selectedPet = data.getSelectionModel().getSelectedItem();
        if (selectedPet != null) {
            cpf.setText(selectedPet.getClienteId());
            nome.setText(selectedPet.getNome());
            sexo.setValue(selectedPet.getSexo().toString());
            raca.setText(String.valueOf(selectedPet.getRacaId()));
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
            pet ultimoPet = data.getItems().get(data.getItems().size() - 1);
            return ultimoPet.getId() + 1;
        }
    }

    @FXML
    private void handleAnchorPaneClick(MouseEvent event) {
        removeListeners();
        data.getSelectionModel().clearSelection();
        setFieldsDisabled(false);
    }

    private void limparCampos() {
        removeListeners();
        cpf.clear();
        nome.clear();
        raca.clear();
    }

    private void addListeners(pet selectedPet) {
        cpf.textProperty().addListener(cpfListener = (obs, oldValue, newValue) -> {
            selectedPet.setClienteId(newValue);
            updateTableData();
            updateEditList(selectedPet);
        });
        nome.textProperty().addListener(nomeListener = (obs, oldValue, newValue) -> {
            selectedPet.setNome(newValue);
            updateTableData();
            updateEditList(selectedPet);
        });
        raca.textProperty().addListener(racaListener = (obs, oldValue, newValue) -> {
            if (Validation.isNumeroInteiro(newValue, "Raça")) {
                selectedPet.setRacaId(Integer.parseInt(newValue));
                updateTableData();
                updateEditList(selectedPet);
            }
        });
        sexo.valueProperty().addListener(sexoListener = (obs, oldValue, newValue) -> {
            selectedPet.setSexo(newValue.equals("MACHO") ? Sexo.MACHO : Sexo.FEMEA);
            updateTableData();
            updateEditList(selectedPet);
        });
    }

    private void updateEditList(pet editedPet) {
        if (!petsEditados.contains(editedPet)) {
            petsEditados.add(editedPet);
        }
    }

    private void updateTableData() {
        data.refresh(); 
    }

    private void removeListeners() {
        if (cpfListener != null) {
            cpf.textProperty().removeListener(cpfListener);
        }
        if (nomeListener != null) {
            nome.textProperty().removeListener(nomeListener);
        }
        if (racaListener != null) {
            raca.textProperty().removeListener(racaListener);
        }
        if (sexoListener != null) {
            sexo.valueProperty().removeListener(sexoListener);
        }
    }
}