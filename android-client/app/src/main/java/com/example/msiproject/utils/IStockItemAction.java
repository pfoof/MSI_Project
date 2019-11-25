package com.example.msiproject.utils;

public interface IStockItemAction {

    public void deleteItem(int id);
    public void editItem(int id);
    public void quantity(int id, int delta);
    public boolean canDelete();
    public boolean canEdit();
    public boolean canQuantity();

}
