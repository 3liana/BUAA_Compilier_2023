package paser_package.stmt_package;

import paser_package.Block;
import paser_package.Stmt;

public class StmtBlock extends Stmt {
    private Block block;
    public StmtBlock(Block block) {
        this.block = block;
    }
}
