package Generator;

public abstract class Generator {
    Object value;
    public Object get() {
        return value;
    }
    public void set(Object value) {
        this.value = value;
    }
    public void print(){
        System.out.print(value+" ");
    }
    public void print(Object value){
        this.value = value;
        print();
    }
}