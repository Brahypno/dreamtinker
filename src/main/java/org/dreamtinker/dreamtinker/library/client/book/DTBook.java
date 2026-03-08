package org.dreamtinker.dreamtinker.library.client.book;

import net.minecraft.resources.ResourceLocation;
import org.dreamtinker.dreamtinker.common.Items.DTBookItem;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.client.book.transformer.BookTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ModifierTagInjectorTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.ToolTagInjectorTransformer;
import slimeknights.tconstruct.library.client.book.sectiontransformer.materials.TierRangeMaterialSectionTransformer;

import static org.dreamtinker.dreamtinker.library.DreamtinkerBookIDs.TRANSMUTE_BOOK_ID;

public class DTBook {
    public static final BookData HYPNAGOGIC_TRANSMUTE = BookLoader.registerBook(TRANSMUTE_BOOK_ID, false, false);
    private static final BookData[] ALL_BOOKS = {HYPNAGOGIC_TRANSMUTE};

    public static void initBook() {

        HYPNAGOGIC_TRANSMUTE.addTransformer(ToolTagInjectorTransformer.INSTANCE);
        HYPNAGOGIC_TRANSMUTE.addTransformer(ModifierTagInjectorTransformer.INSTANCE);
        addStandardData(HYPNAGOGIC_TRANSMUTE, TRANSMUTE_BOOK_ID);
    }

    /**
     * Gets the book for the enum value
     *
     * @param bookType Book type
     * @return Book
     */
    public static BookData getBook(DTBookItem.BookType bookType) {
        return switch (bookType) {
            case HYPNAGOGIC_TRANSMUTE -> HYPNAGOGIC_TRANSMUTE;
        };
    }

    /**
     * Adds the repository and the relevant transformers to the books
     *
     * @param book Book instance
     * @param id   Book ID
     */
    @SuppressWarnings("removal")
    private static void addStandardData(BookData book, ResourceLocation id, BookTransformer... extraTransformers) {
        book.addRepository(new FileRepository(new ResourceLocation(id.getNamespace(), "book/" + id.getPath())));
        book.addTransformer(BookTransformer.indexTranformer());
        book.addTransformer(TierRangeMaterialSectionTransformer.INSTANCE);

        // any transformers that go after tier range
        for (BookTransformer transformer : extraTransformers) {
            book.addTransformer(transformer);
        }

        // padding needs to be last to ensure page counts are right
        book.addTransformer(BookTransformer.paddingTransformer());
    }
}
