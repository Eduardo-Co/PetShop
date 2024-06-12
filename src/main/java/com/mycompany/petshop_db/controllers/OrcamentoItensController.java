package com.mycompany.petshop_db.controllers;


import com.mycompany.petshop_db.dao.DatabaseOrcamento;
import com.mycompany.petshop_db.models.orcamentoitem;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class OrcamentoItensController implements Initializable {

    private String situacao = "";

    @FXML
    private TableView<orcamentoitem> data;

    @FXML
    private TableColumn<orcamentoitem, Integer> idtable;

    @FXML
    private TableColumn<orcamentoitem, Integer> cabecalhotable;

    @FXML
    private TableColumn<orcamentoitem, Integer> produtotable;

    @FXML
    private TableColumn<orcamentoitem, Integer> quantidadetable;

    @FXML
    private TableColumn<orcamentoitem, BigDecimal> precotable;

    @FXML
    private Button excluir;

    @FXML
    private Button salvar;

    @FXML
    private Text text;

    @FXML
    private TextField quantidade;

    @FXML
    private TextField preco;
    
    @FXML
    private TextField produto;

    @FXML
    private Button editar;

    @FXML
    private Button cancelar;

    private List<orcamentoitem> itemExcluidos = new ArrayList<>();
    private List<orcamentoitem> itemEditados = new ArrayList<>();
    private List<orcamentoitem> itemAdicionados = new ArrayList<>();

    private ChangeListener<String> precoListener;
    private ChangeListener<String> quantidadeListener;
    private ChangeListener<String> produtoListener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.print("oi");
        idtable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        cabecalhotable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrcamentoCabecalhoId()).asObject());
        produtotable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProdutoId()).asObject());
        quantidadetable.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantidade()).asObject());
        precotable.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecoUnitario()));
        
        
        getItens();
        
        data.setOnMouseClicked(this::handleTableClick);
    }

    private void getItens() {
        removeListeners();
        itemExcluidos.clear();
        itemEditados.clear();
        
        List<orcamentoitem> orcamentoItens = DatabaseOrcamento.getItens();
        data.getSelectionModel().clearSelection();
        
 
        ObservableList<orcamentoitem> observableItemList = FXCollections.observableArrayList(orcamentoItens);
        data.setItems(observableItemList);
    }
    
    @FXML
    private void handleVoltar(){
        try {
            loadPage("/fxml/ExibirOrcamento.fxml", "Visualizar Orçamento");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAdicionar(ActionEvent event) {

       if (Validation.isAnyFieldEmpty(
            quantidade.getText(),
            preco.getText(),
            produto.getText()
        )) {
        mostrarAlerta("Todos os campos devem ser preenchidos.");
        return;
       }
        BigDecimal precoNum;
        int quantidadeNum;
        int produtoNum;
        
        
        try {
            precoNum = new BigDecimal(preco.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Preço deve ser um número válido.");
            return;
        }

        try {
            quantidadeNum = Integer.parseInt(quantidade.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Quantidade deve ser um número inteiro válido.");
            return;
        }
        
        try {
            produtoNum = Integer.parseInt(produto.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Produto deve ser um número inteiro válido.");
            return;
        }

        int novoId = determinarNovoId();
        orcamentoitem newItem = new orcamentoitem(precoNum, quantidadeNum, produtoNum);
        newItem.setId(novoId);

        data.getItems().add(newItem);
        itemAdicionados.add(newItem);
        situacao = "a";
    }
       
    @FXML
    private void handleExcluir(ActionEvent event) {
        removeListeners();
        ObservableList<orcamentoitem> selectedItems = data.getSelectionModel().getSelectedItems();
        situacao = "e";
        for (orcamentoitem item : selectedItems) {
            itemExcluidos.add(item);
        }
        data.getItems().removeAll(selectedItems);
    }

    @FXML
    private void handleEditar(ActionEvent event) {
        orcamentoitem item = data.getSelectionModel().getSelectedItem();

        if (item != null) {
            situacao = "edt";
            setFieldsDisabled(false);
            addListeners(item);
        }
    }

    @FXML
    private void handleSalvar() {
        removeListeners();

        switch (situacao) {
            case "e":
                for (orcamentoitem item : itemEditados) {
                    DatabaseOrcamento.editarItem(item);
                }
                for (orcamentoitem item : itemExcluidos) {
                    DatabaseOrcamento.excluirItem(item);
                }
                mostrarAlerta("Orçamento(s) excluídos com sucesso!");
                break;
            case "edt":
                for (orcamentoitem item : itemExcluidos) {
                    DatabaseOrcamento.excluirItem(item);
                }
                for (orcamentoitem item : itemEditados) {
                    DatabaseOrcamento.editarItem(item);
                }
                mostrarAlerta("Orçamento(s) editados com sucesso!");
                break;
            case "a":
                for(orcamentoitem item: itemAdicionados){
                    DatabaseOrcamento.salvarOrcamentoItem(item, DatabaseOrcamento.getLast());
                }
                mostrarAlerta("Orçamento(s) salvos com sucesso!");
                break;
            default:
                break;
        }
        itemExcluidos.clear();
        itemEditados.clear();
        getItens();
    }
    
    @FXML
    private void handleTableClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            long clickTime = System.currentTimeMillis();
            orcamentoitem selectedOrcamento = data.getSelectionModel().getSelectedItem();

            if (selectedOrcamento != null) {
                produto.setText(String.valueOf(selectedOrcamento.getProdutoId()));
                preco.setText(String.valueOf(selectedOrcamento.getPrecoUnitario()));
                quantidade.setText(String.valueOf(selectedOrcamento.getQuantidade()));
                setFieldsDisabled(true);

            }
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        itemExcluidos.clear();
        itemEditados.clear();
        getItens();
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void removeListeners() {
        if (precoListener != null) {
            preco.textProperty().removeListener(precoListener);
        }
        if (quantidadeListener != null) {
            quantidade.textProperty().removeListener(quantidadeListener);
        }
        if (produtoListener != null) {
            produto.textProperty().removeListener(produtoListener);
        }
    }

    private void addListeners(orcamentoitem selectedOrcamento) {
        preco.textProperty().addListener(precoListener = (obs, oldValue, newValue) -> {
            selectedOrcamento.setPrecoUnitario(new BigDecimal(newValue));
            updateTableData();
            updateEditList(selectedOrcamento);
        });

        quantidade.textProperty().addListener(quantidadeListener = (obs, oldValue, newValue) -> {
            selectedOrcamento.setQuantidade(Integer.parseInt(newValue));
            updateTableData();
            updateEditList(selectedOrcamento);
        });

        produto.textProperty().addListener(produtoListener = (obs, oldValue, newValue) -> {
            selectedOrcamento.setProdutoId(Integer.parseInt(newValue));
            updateTableData();
            updateEditList(selectedOrcamento);
        });
    }

    private void updateEditList(orcamentoitem editedOrcamento) {
        if (!itemEditados.contains(editedOrcamento)) {
            itemEditados.add(editedOrcamento);
        }
    }

    private void updateTableData() {
        data.refresh();
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
    private void setFieldsDisabled(boolean disabled) {
        quantidade.setDisable(disabled);
        preco.setDisable(disabled);
        produto.setDisable(disabled);
    }
    private int determinarNovoId() {
        if (data.getItems().isEmpty()) {
            return 1;
        } else {
            orcamentoitem ultimoItem = data.getItems().get(data.getItems().size() - 1);
            return ultimoItem.getId() + 1;
        }
    }
}
