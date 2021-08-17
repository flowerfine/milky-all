package cn.sliew.milky.common.constant;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

public class DefaultTagSet implements TagSet {

    private static final AtomicReferenceFieldUpdater<DefaultTagSet, Tag[]> TAGS_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(DefaultTagSet.class, Tag[].class, "tags");
    private static final Tag[] EMPTY_TAGS = new Tag[0];

    /**
     * Similarly to {@code Arrays::binarySearch} it perform a binary search optimized for this use case, in order to
     * save polymorphic calls (on comparator side) and unnecessary class checks.
     */
    private static int searchTagByKey(Tag[] sortedTags, Tag key) {
        int low = 0;
        int high = sortedTags.length - 1;

        while (low <= high) {
            int mid = low + high >>> 1;
            Tag midVal = sortedTags[mid];
            if (midVal.equals(key)) {
                return mid;
            }
            int midValKeyId = midVal.id();
            int keyId = key.id();
            assert midValKeyId != keyId;
            boolean searchRight = midValKeyId < keyId;
            if (searchRight) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -(low + 1);
    }

    private static void orderedCopyOnInsert(Tag[] sortedSrc, int srcLength, Tag[] copy, Tag toInsert) {
        // let's walk backward, because as a rule of thumb, toInsert.key.id() tends to be higher for new keys
        final int id = toInsert.id();
        int i;
        for (i = srcLength - 1; i >= 0; i--) {
            Tag tag = sortedSrc[i];
            assert tag.id() != id;
            if (tag.id() < id) {
                break;
            }
            copy[i + 1] = sortedSrc[i];
        }
        copy[i + 1] = toInsert;
        final int toCopy = i + 1;
        if (toCopy > 0) {
            System.arraycopy(sortedSrc, 0, copy, 0, toCopy);
        }
    }


    private volatile Tag[] tags = EMPTY_TAGS;

    @Override
    public Collection<Tag> tags() {
        return Arrays.asList(tags);
    }

    @Override
    public void tag(Tag tag) {
        checkNotNull(tag);

        for (; ; ) {
            final Tag[] tags = this.tags;
            final int index = searchTagByKey(tags, tag);
            final Tag[] newTags;
            if (index < 0) {
                final int count = tags.length;
                newTags = new Tag[count + 1];
                orderedCopyOnInsert(tags, count, newTags, tag);

                if (TAGS_UPDATER.compareAndSet(this, tags, newTags)) {
                    return;
                }
            }
        }
    }

    @Override
    public boolean hasTag(Tag tag) {
        checkNotNull(tag);

        return searchTagByKey(tags, tag) >= 0;
    }
}
