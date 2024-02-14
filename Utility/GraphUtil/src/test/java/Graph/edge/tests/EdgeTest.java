package Graph.edge.tests;

import Graph.edges.DirectedEdge;
import org.junit.jupiter.api.Test;

public class EdgeTest {

    @Test
    void simpleTest(){
        // simple test to make sure DirectedEdge inherits from Edge
        DirectedEdge edge = new DirectedEdge("A", "B", 1.0);
        assert(edge.from().equals("A"));
        assert(edge.to().equals("B"));
        assert(edge.weight() == 1.0);
        //make sure the other methods from Edge are inherited
        assert(edge.either().equals("A"));
        assert(edge.other("A").equals("B"));
        assert(edge.other("B").equals("A"));
        assert(edge.compareTo(new DirectedEdge("A", "B", 1.0)) == 0);

    }
}
