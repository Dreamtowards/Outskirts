//{

func (abc,b)

//}


{

class Color {
    var r = 5;
    var g;
    var b;

    def prt() {

        print("abc");
        def doprt() {
            print("Inner Really Print");
        }
        return doprt;
    }
}

class Pos {
    var x;
    var y;
    var color = Color();

    def add() {
        return x + y;
    }

    def getColor() {
        return color;
    }

    print(123);
}

var col = Pos().color;

col = col.prt();

col();

}