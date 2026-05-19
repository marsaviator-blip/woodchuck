package org.woodchuck.dtos;

import java.io.Serializable;

public class MaterialStructureParams implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String  material_id;
    private String  _fields;
    private boolean deprecated;
    private int     _per_page;
    private int     _skip;
    private int     _limit;
    private String license;

    public MaterialStructureParams() {
    }
    
    public MaterialStructureParams(String material_id, String _fields, boolean deprecated, int _per_page, int _skip, int _limit, String license) {
        this.material_id = material_id;
        this._fields = _fields;
        this.deprecated = deprecated;
        this._per_page = _per_page;
        this._skip = _skip;
        this._limit = _limit;
        this.license = license;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public String get_fields() {
        return _fields;
    }   

    public boolean isDeprecated() {
        return deprecated;
    }

    public int get_per_page() {
        return _per_page;
    }

    public int get_skip() {
        return _skip;
    }

    public int get_limit() {
        return _limit;
    }

    public String getLicense() {
        return license;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public void set_fields(String _fields) {
        this._fields = _fields;
    }   

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }   

    public void set_per_page(int _per_page) {
        this._per_page = _per_page;
    }   

    public void set_skip(int _skip) {
        this._skip = _skip;
    }   

    public void set_limit(int _limit) {
        this._limit = _limit;
    }   

    public void setLicense(String license) {
        this.license = license;
    }   

}
