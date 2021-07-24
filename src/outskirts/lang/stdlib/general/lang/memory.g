package general.lang;

@final
class memory {

    @native
    static T get<T>(ptr addr);

    @native
    static void set<T>(ptr addr, T o);

}