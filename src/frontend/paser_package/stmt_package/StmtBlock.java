package frontend.paser_package.stmt_package;

import frontend.paser_package.Block;
import frontend.paser_package.Stmt;

public class StmtBlock extends Stmt {
    private Block block;
    public StmtBlock(Block block) {
        this.block = block;
    }
}
