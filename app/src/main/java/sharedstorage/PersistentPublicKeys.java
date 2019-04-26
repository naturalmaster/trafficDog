package sharedstorage;

public enum PersistentPublicKeys implements IPersistentPublicKeys {
    FIRST_START,
    SORT_TYPE   //排序方式
    ;

    @Override
    public String getString() {
        return this.name();
    }

}
