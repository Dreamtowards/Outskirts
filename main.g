{

class Main {

    class Entity {

        int age = 100;

        String name;

        void print(boolean v) {
            if (v) {
                prt("Print some V.");
            }
            prt("Entity{age: "+age+", name: "+name+"}");
        }
    }

    function log = (s) {  prt("[LOG]: "+s);  };

    int i = 10 * 2;
    log(i);

    Entity e = new Entity();

    e.print(1);

}


new Main();


}