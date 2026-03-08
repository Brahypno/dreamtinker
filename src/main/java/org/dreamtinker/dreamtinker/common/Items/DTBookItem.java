package org.dreamtinker.dreamtinker.common.Items;

import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.library.client.book.DTBook;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.client.book.BookScreenOpener;
import slimeknights.mantle.item.AbstractBookItem;

public class DTBookItem extends AbstractBookItem {
    private final BookType bookType;

    public DTBookItem(Properties props, BookType bookType) {
        super(props);
        this.bookType = bookType;
    }

    @Override
    public @NotNull BookScreenOpener getBook(@NotNull ItemStack stack) {
        return DTBook.getBook(bookType);
    }

    /**
     * Simple enum to allow selecting the book on the client
     */
    public enum BookType {
        HYPNAGOGIC_TRANSMUTE
    }
}
