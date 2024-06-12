package com.mycompany.petshop_db.controllers;

import com.mycompany.petshop_db.dao.DatabaseVeterinarios;
import com.mycompany.petshop_db.models.veterinarios;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class VeterinariosController implements Initializable {
    
    private Menu activeMenu;
    private String situacao = "";

    @FXML
    private AnchorPane borderPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu principal;
    @FXML
    private Menu clientes;
    @FXML
    private Menu pet;
    @FXML
    private Menu produtos;
    @FXML
    private Menu veterinarios;
    @FXML
    private Menu racas;
    @FXML
    private Menu atendentes;
    @FXML
    private AnchorPane mainContent;
    @FXML
    private TableView<veterinarios> data;
    @FXML
    private TableColumn<veterinarios, String> crmvtable;
    @FXML
    private TableColumn<veterinarios, String> nometable;
    @FXML
    private TableColumn<veterinarios, String> especializacaotable;
    @FXML
    private TableColumn<veterinarios, String> telefonetable;
    @FXML
    private TextField nome;
    @FXML
    private Button adicionar;
    @FXML
    private Button excluir;
    @FXML
    private Button salvar;
    @FXML
    private TextField crmv;
    @FXML
    private TextField telefone;
    @FXML
    private TextField pesquisar;
    @FXML
    private Button btnpesquisar;
    @FXML
    private Button btnlimpar;
    @FXML
    private TextField especializacao;

    
    private List<veterinarios> veterinariosAdicionados = new ArrayList<>();
    private List<veterinarios> veterinariosExcluidos = new ArrayList<>();
    private List<veterinarios> veterinariosEditados = new ArrayList<>();
    
    private ChangeListener<String> crmvListener;
    private ChangeListener<String> nomeListener;
    private ChangeListener<String> especializacaoListener;
    private ChangeListener<String> telefoneListener;  

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
        Menu defaultMenu = menuBar.getMenus().get(4);
        setActiveMenu(defaultMenu);

        crmvtable.setCellValueFactory(new PropertyValueFactory<>("crmv"));
        nometable.setCellValueFactory(new PropertyValueFactory<>("nome"));
        especializacaotable.setCellValueFactory(new PropertyValueFactory<>("especializacao"));
        telefonetable.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        loadVeterinariosData();
        
        data.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            removeListeners();
            if (newSelection != null) {
                handleSelecionarItem();
                crmv.setDisable(false);
                crmv.setDisable(true);
            }
        });
    }

    private void loadPage(String page, String menuText) throws IOException {
        Stage currentStage = (Stage) borderPane.getScene().getWindow();
        currentStage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
        Parent root = loader.load();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle(menuText);
        newStage.show();
    }

    private void loadVeterinariosData() {
        veterinariosAdicionados.clear();
        veterinariosExcluidos.clear();
        veterinariosEditados.clear();
        data.getSelectionModel().clearSelection();
        limparCampos();
        removeListeners();
        
        List<veterinarios> veterinariosList = DatabaseVeterinarios.getVeterinarios();
        ObservableList<veterinarios> observableVeterinariosList = FXCollections.observableArrayList(veterinariosList);
        data.setItems(observableVeterinariosList);
    }

    @FXML
    private void handleAdicionar(ActionEvent event) {
        String crmvText = crmv.getText();

        if (Validation.isAnyFieldEmpty(
                crmvText,
                nome.getText(),
                especializacao.getText(),
                telefone.getText()
        )) {
            return;
        }

        if (!Validation.isTelefoneValido(telefone.getText())) {
            return;
        }


        veterinarios novoVeterinario = new veterinarios(
                crmvText,
                nome.getText(),
                especializacao.getText(),
                telefone.getText()
        );

        data.getItems().add(novoVeterinario);
        veterinariosAdicionados.add(novoVeterinario);
        situacao = "a";
    }

    @FXML
    private void handleExcluir(ActionEvent event) {
        removeListeners();
        ObservableList<veterinarios> selectedItems = data.getSelectionModel().getSelectedItems();
        
        if (!veterinariosAdicionados.isEmpty()) {
            mostrarAlerta("Salve o conteúdo antes de excluir.");
            return;
        }
        situacao = "e";
        
        for (veterinarios veterinario : selectedItems) {
            veterinariosAdicionados.remove(veterinario);
            veterinariosExcluidos.add(veterinario);
        }
        data.getItems().removeAll(selectedItems);
    }
    
    @FXML
    private void handleEditar(ActionEvent event) {
        veterinarios selectedPet = data.getSelectionModel().getSelectedItem();
        if (!veterinariosAdicionados.isEmpty()) {
           mostrarAlerta("Salve o conteúdo antes de Editar");
           return; 
        }
        if (selectedPet != null) {
            crmv.setDisable(true);
            setFieldsDisabled(false);
            situacao = "edt";
            addListeners(selectedPet);
        }
    }
    
     @FXML
    private void handleSalvar(ActionEvent event) {
        removeListeners();

        switch (situacao) {
            case "a":
                DatabaseVeterinarios.salvarVeterinarios(veterinariosAdicionados);
                mostrarAlerta("Veterinário(s) adicionados com sucesso!");
                break;
            case "e":
                if (!veterinariosEditados.isEmpty()) {
                    for (veterinarios veterinario : veterinariosEditados) {
                        DatabaseVeterinarios.editarVeterinario(veterinario);
                    }
                }
                for (veterinarios veterinario : veterinariosExcluidos) {
                    DatabaseVeterinarios.excluirVeterinario(veterinario);
                }
                mostrarAlerta("Veterinário(s) excluidos com sucesso!");
                break;
            case "edt":
                
                if (!Validation.isTelefoneValido(telefone.getText())) {
                    return;
                }
                
                if (!veterinariosExcluidos.isEmpty()) {
                    for (veterinarios produto : veterinariosExcluidos) {
                        DatabaseVeterinarios.excluirVeterinario(produto);
                    }
                }
                for (veterinarios veterinario : veterinariosEditados) {
                    DatabaseVeterinarios.editarVeterinario(veterinario);
                }
                mostrarAlerta("Veterinário(s) editados com sucesso!");
                break;
            default:
                break;
        }
        veterinariosAdicionados.clear();
        veterinariosExcluidos.clear();
        veterinariosEditados.clear();
        loadVeterinariosData();
    }     

    @FXML
    private void handlePesquisar(ActionEvent event) {
        String nomeSearch = nome.getText();
        String especializacaoSearch = especializacao.getText();
        String crmvSearch = crmv.getText();
        String telefoneSearch = telefone.getText();

        List<veterinarios> resultados = DatabaseVeterinarios.procurarVeterinario(nomeSearch, especializacaoSearch, crmvSearch, telefoneSearch);
        ObservableList<veterinarios> observableResultados = FXCollections.observableArrayList(resultados);
        data.setItems(observableResultados);
    }

    @FXML
    private void handleLimpar(ActionEvent event) {
        loadVeterinariosData();
        limparCampos();
        crmv.setDisable(false);
        setFieldsDisabled(false);
        loadVeterinariosData();
    }
    @FXML
    private void handleCancelar(ActionEvent event) {
        veterinariosAdicionados.clear();
        veterinariosExcluidos.clear();
        veterinariosEditados.clear();
        loadVeterinariosData();
    }
    @FXML
    private void handleSelecionarItem() {
        veterinarios selectedVeterinario = data.getSelectionModel().getSelectedItem();
        if (selectedVeterinario != null) {
            crmv.setText(selectedVeterinario.getCrmv());
            nome.setText(selectedVeterinario.getNome());
            especializacao.setText(selectedVeterinario.getEspecializacao());
            telefone.setText(selectedVeterinario.getTelefone());
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


    @FXML
    private void handleAnchorPaneClick(MouseEvent event) {
        removeListeners();
        data.getSelectionModel().clearSelection();
        setFieldsDisabled(false);
    }

    private void limparCampos() {
        removeListeners();
        nome.clear();
        crmv.clear();
        especializacao.clear();
        telefone.clear();
    }

    private void addListeners(veterinarios selectedVeterinario) {
        nome.textProperty().addListener(nomeListener = (obs, oldValue, newValue) -> {
            selectedVeterinario.setNome(newValue);
            updateTableData();
            updateEditList(selectedVeterinario);
        });
        crmv.textProperty().addListener(crmvListener = (obs, oldValue, newValue) -> {
            selectedVeterinario.setCrmv(newValue);
            updateTableData();
            updateEditList(selectedVeterinario);
        });
        especializacao.textProperty().addListener(especializacaoListener = (obs, oldValue, newValue) -> {
            selectedVeterinario.setEspecializacao(newValue);
            updateTableData();
            updateEditList(selectedVeterinario);
        });
        telefone.textProperty().addListener(telefoneListener = (obs, oldValue, newValue) -> {
            selectedVeterinario.setTelefone(newValue);
            updateTableData();
            updateEditList(selectedVeterinario);
        });
    }

    private void updateEditList(veterinarios editedVeterinario) {
        if (!veterinariosEditados.contains(editedVeterinario)) {
            veterinariosEditados.add(editedVeterinario);
        }
    }

    private void updateTableData() {
        data.refresh(); 
    }

    private void removeListeners() {
        if (nomeListener != null) {
            nome.textProperty().removeListener(nomeListener);
        }
        if (crmvListener != null) {
            crmv.textProperty().removeListener(crmvListener);
        }
        if (especializacaoListener != null) {
            especializacao.textProperty().removeListener(especializacaoListener);
        }
        if (telefoneListener != null) {
            telefone.textProperty().removeListener(telefoneListener);
        }
    }

    private void setFieldsDisabled(boolean disabled) {
        nome.setDisable(disabled);
        especializacao.setDisable(disabled);
        telefone.setDisable(disabled);
    }
}