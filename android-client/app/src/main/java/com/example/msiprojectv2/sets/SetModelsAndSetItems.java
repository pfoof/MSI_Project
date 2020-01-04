package com.example.msiprojectv2.sets;

import com.example.msiprojectv2.utils.SetItem;
import com.example.msiprojectv2.utils.SetModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SetModelsAndSetItems {

    @JsonProperty("models")
    public List<SetModel> models;

    @JsonProperty("setitems")
    public List<SetItem> items;

}