package frontend.paser_package;

import java.util.ArrayList;

public class Block {
    private ArrayList<BlockItem> blockItems;

    public Block() {
        this.blockItems = new ArrayList<>();
    }

    public void appendBlockItem(BlockItem blockItem) {
        this.blockItems.add(blockItem);
    }
}
