namespace outskirts::entity;

using outskirts::util::Tickable;
using outskirts::util::register::Registrable;
using outskirts::storage::Savable;

class Entity : Tickable, Registrable, Savable {



    void read(DObject mp) override const {


    }

    void write(DObject mp) override {

    }

    void onTick() override {

    }

}