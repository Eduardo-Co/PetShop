package com.mycompany.petshop_db.controllers;

import com.mycompany.petshop_db.dao.DatabaseInitializer;
import com.mycompany.petshop_db.dao.DatabaseOrcamento;
import com.mycompany.petshop_db.models.orcamentocabecalho;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.input.MouseButton;

public class ExibirOrcamentoController implements Initializable {

    private long lastClickTime = 0;
    private Menu activeMenu;
    private String situacao = "";
    private orcamentocabecalho lastSelectedItem = null;

    @FXML
    private MenuBar menuBar;

    @FXML
    private TableView<orcamentocabecalho> data;

    @FXML
    private TableColumn<orcamentocabecalho, Integer> idtable;

    @FXML
    private TableColumn<orcamentocabecalho, String> cpftable;

    @FXML
    private TableColumn<orcamentocabecalho, String> formapagamentotable;

    @FXML
    private TableColumn<orcamentocabecalho, String> datatable;

    @FXML
    private TableColumn<orcamentocabecalho, Integer> petidtable;

    @FXML
    private ComboBox<String> formapagamento;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField cpf;

    @FXML
    private TextField petid;

    @FXML
    private Button excluir;

    @FXML
    private Button salvar;

    @FXML
    private Button editar;

    @FXML
    private Button pesquisar;

    @FXML
    private Button limpar;

    @FXML
    private Button cancelar;
    
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
    private void handlePet() throws IOException {
        loadPage("/fxml/Pet.fxml", "Pet");
    }

    @FXML
    private void handleProdutos() throws IOException {
        loadPage("/fxml/Produtos.fxml", "Produtos");
    }

    @FXML
    private void handleVeterinarios() throws IOException {
        loadPage("/fxml/Veterinarios.fxml", "Veterinarios");
    }

    @FXML
    private void handleRacas() throws IOException {
        loadPage("/fxml/Racas.fxml", "Raças");
    }

    @FXML
    private void handleAtendentes() throws IOException {
        loadPage("/fxml/Atendentes.fxml", "Atendentes");
    }

    private List<orcamentocabecalho> cabecalhoExcluidos = new ArrayList<>();
    private List<orcamentocabecalho> cabecalhoEditados = new ArrayList<>();

    private ChangeListener<LocalDate> dataListener;
    private ChangeListener<String> formapagamentoListener;
    private ChangeListener<String> cpfListener;
    private ChangeListener<String> petListener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Menu defaultMenu = menuBar.getMenus().get(0);
        setActiveMenu(defaultMenu);

        idtable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        cpftable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClienteId()));
        formapagamentotable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormaPagamentoId()));
        datatable.setCellValueFactory(cellData -> {
            LocalDate data = cellData.getValue().getDataOrcamento();
            String dataFormatada = "";

            if (data != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dataFormatada = data.format(formatter);
            }

            return new SimpleStringProperty(dataFormatada);
        });
        petidtable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPetId()).asObject());

        loadFormasPagamento();
        loadCabecalhoData();

        data.setOnMouseClicked(this::handleTableClick);

    }

    private void loadCabecalhoData() {
        cabecalhoExcluidos.clear();
        cabecalhoEditados.clear();
        data.getSelectionModel().clearSelection();
        limparCampos();
        removeListeners();

        List<orcamentocabecalho> orcamentocabecalho = DatabaseOrcamento.getCabecalho();
        ObservableList<orcamentocabecalho> observableCabecalhoList = FXCollections.observableArrayList(orcamentocabecalho);
        data.setItems(observableCabecalhoList);
    }

    @FXML
    private void handleExcluir(ActionEvent event) {
        removeListeners();
        ObservableList<orcamentocabecalho> selectedItems = data.getSelectionModel().getSelectedItems();
        situacao = "e";
        for (orcamentocabecalho cabecalho : selectedItems) {
            cabecalhoExcluidos.add(cabecalho);
        }
        data.getItems().removeAll(selectedItems);
    }

    @FXML
    private void handleEditar(ActionEvent event) {
        orcamentocabecalho cabecalho = data.getSelectionModel().getSelectedItem();

        if (cabecalho != null) {
            setFieldsDisabled(false);
            situacao = "edt";
            addListeners(cabecalho);
        }
    }

    @FXML
    private void handleSalvar() {
        removeListeners();

        switch (situacao) {
            case "e":
                for (orcamentocabecalho cabecalho : cabecalhoEditados) {
                    DatabaseOrcamento.editarCabecalho(cabecalho);
                }
                for (orcamentocabecalho cabecalho : cabecalhoExcluidos) {
                    DatabaseOrcamento.excluirCabecalho(cabecalho);
                }
                mostrarAlerta("Orçamento(s) excluídos com sucesso!");
                break;
            case "edt":
                for (orcamentocabecalho cabecalho : cabecalhoExcluidos) {
                    DatabaseOrcamento.excluirCabecalho(cabecalho);
                }
                for (orcamentocabecalho cabecalho : cabecalhoEditados) {
                    if (!Validation.isCPFValido(cabecalho.getClienteId())) {
                        return;
                    }
                    if (!Validation.isNumeroInteiro(String.valueOf(cabecalho.getPetId()), "Número")) {
                        return;
                    }
                    DatabaseOrcamento.editarCabecalho(cabecalho);
                }
                mostrarAlerta("Orçamento(s) editados com sucesso!");
                break;
            default:
                break;
        }
        cabecalhoExcluidos.clear();
        cabecalhoEditados.clear();
        loadCabecalhoData();
    }
    
    @FXML
    private void handleTableClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            long clickTime = System.currentTimeMillis();
            orcamentocabecalho selectedOrcamento = data.getSelectionModel().getSelectedItem();

            if (clickTime - lastClickTime < 300 && lastSelectedItem != null && lastSelectedItem.equals(selectedOrcamento)) {
                try{
                    DatabaseOrcamento.setLast(selectedOrcamento.getId());
                    loadPage("/fxml/OrcamentoItens.fxml", "Itens do orçamento");
                }catch(IOException e){
                }
            }

            lastClickTime = clickTime;
            lastSelectedItem = selectedOrcamento;

            if (selectedOrcamento != null) {
                cpf.setText(selectedOrcamento.getClienteId());
                petid.setText(String.valueOf(selectedOrcamento.getPetId()));
                formapagamento.setValue(selectedOrcamento.getFormaPagamentoId());
                datePicker.setValue(selectedOrcamento.getDataOrcamento());
                setFieldsDisabled(true);
            }
        }
    }


    @FXML
    private void handlePesquisar(ActionEvent event) {
        removeListeners();

        String pagamentoSearch = formapagamento.getValue();
        String cpfSearch = cpf.getText();
        LocalDate dataSearch = datePicker.getValue();
        String petSearch = petid.getText();

        List<orcamentocabecalho> resultados = DatabaseOrcamento.procurarCabecalho(pagamentoSearch, cpfSearch, dataSearch, petSearch);
        ObservableList<orcamentocabecalho> observableResultados = FXCollections.observableArrayList(resultados);
        data.setItems(observableResultados);
    }

    private void limparCampos() {
        cpf.clear();
        petid.clear();
        formapagamento.getSelectionModel().clearSelection();
        datePicker.setValue(null);
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        cabecalhoExcluidos.clear();
        cabecalhoEditados.clear();
        loadCabecalhoData();
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void loadFormasPagamento() {
        List<String> formasPagamentoList = DatabaseInitializer.getFormasPagamento();
        System.out.println("Lista de formas de pagamento recebida: " + formasPagamentoList);

        ObservableList<String> observableFormasPagamentoList = FXCollections.observableArrayList(formasPagamentoList);
        formapagamento.setItems(observableFormasPagamentoList);
        formapagamento.getSelectionModel().selectFirst();
    }


    private void setFieldsDisabled(boolean disabled) {
        cpf.setDisable(disabled);
        petid.setDisable(disabled);
        datePicker.setDisable(disabled);
        formapagamento.setDisable(disabled);
    }

    private void removeListeners() {
        if (cpfListener != null) {
            cpf.textProperty().removeListener(cpfListener);
        }
        if (dataListener != null) {
            datePicker.valueProperty().removeListener(dataListener);
        }
        if (formapagamentoListener != null) {
            formapagamento.valueProperty().removeListener(formapagamentoListener);
        }
        if (petListener != null) {
            petid.textProperty().removeListener(petListener);
        }
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

    private void addListeners(orcamentocabecalho selectedOrcamento) {
        cpf.textProperty().addListener(cpfListener = (obs, oldValue, newValue) -> {
            selectedOrcamento.setClienteId(newValue);
            updateTableData();
            updateEditList(selectedOrcamento);
        });
        formapagamento.valueProperty().addListener(formapagamentoListener = (obs, oldValue, newValue) -> {
            selectedOrcamento.setFormaPagamentoId(newValue);
            updateTableData();
            updateEditList(selectedOrcamento);
        });
        datePicker.valueProperty().addListener(dataListener = (obs, oldValue, newValue) -> {
            selectedOrcamento.setDataOrcamento(newValue);
            updateTableData();
            updateEditList(selectedOrcamento);
        });

        petid.textProperty().addListener(petListener = (obs, oldValue, newValue) -> {
            selectedOrcamento.setPetId(Integer.parseInt(newValue));
            updateTableData();
            updateEditList(selectedOrcamento);
        });
    }

    private void updateEditList(orcamentocabecalho editedOrcamento) {
        if (!cabecalhoEditados.contains(editedOrcamento)) {
            cabecalhoEditados.add(editedOrcamento);
        }
    }

    private void updateTableData() {
        data.refresh();
    }
    @FXML
    private void handleLimpar(ActionEvent event){
        loadCabecalhoData();
        limparCampos();
        setFieldsDisabled(false);
        loadCabecalhoData();
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
}
