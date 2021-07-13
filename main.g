5 + -10++ - sth + "ABC"

{



class vec3 : vec<3> {



}

@interface
class hashable {

    @virtual
    int hashcode();

}


class hash_map<K : hashable, V> : map<K, V> {


}

@final
class string : hashable {

    @private
    int length;

    @private
    array<ushort> values;

    @private;
    int hash;

    @inline
    ushort char_at(int i) {
        return values.get(i);
    }

    @private
    @operator.equals
    boolean opimpl_equals(string r) {
        if (r.length != length)
            return false;
        if (r.hashcode() != hashcode())
            return false;

        int len = length;
        for (int i = 0;i < len;i++) {
            if (r.char_at(i) != char_at(i))
                return false;
        }
        return true;
    }

    @override
    int hashcode() {
        if (!hash && values.length > 0) {
            hash = arrays.hashcode(values);
            if (!hash)
                hash = -1;
        }
        return hash;
    }
}


@final
class String : Hashable {

    @private
    int length;

    @private
    array<ushort> values;

    @private;
    int hash;

    @inline
    ushort charAt(int i) {
        return values.get(i);
    }

    @private
    @operator.equals
    boolean opimpl_equals(String r) {
        if (r.length != length)
            return false;
        if (r.hashcode() != hashcode())
            return false;

        int len = length;
        for (int i = 0;i < len;i++) {
            if (r.charAt(i) != charAt(i))
                return false;
        }
        return true;
    }

    @override
    int hashcode() {
        if (!hash && values.length > 0) {
            hash = Arrays.hashcode(values);
            if (!hash)
                hash = -1;
        }
        return hash;
    }
}

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

class Main {

    function log = (s) {
        prt("[LOG]: "+s);
    };

}


int i = 10 * 2;

Entity e = new Entity();

e.print(1);

new Main().log(i);


}