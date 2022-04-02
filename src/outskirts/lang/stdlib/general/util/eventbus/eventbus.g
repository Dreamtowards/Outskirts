

class eventbus {

    @private
    map<class<event>, evhandler> handlers = new hash_map<>();


    void register(function<void, event> handler) {

    }

    @static
    class evhandler {

        function<void, event> hdr;

        int priority;

    }
}