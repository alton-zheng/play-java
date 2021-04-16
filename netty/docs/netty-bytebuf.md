# Netty ByteBuf

&nbsp;

- Direct Known Subclasses:

  AbstractByteBuf, [EmptyByteBuf](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/EmptyByteBuf.html), [SwappedByteBuf](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/SwappedByteBuf.html)

  ------

  ```java
  public abstract class ByteBuf implements ReferenceCounted, Comparable<ByteBuf> 
  ```

  &nbsp;

  A random and sequential accessible sequence of zero or more bytes (octets). This interface provides an abstract view for one or more primitive byte arrays (`byte[]`) and [NIO buffers](https://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html?is-external=true).

  ### Creation of a buffer

  It is recommended to create a new buffer using the helper methods in [`Unpooled`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/Unpooled.html) rather than calling an individual implementation's constructor.

  ### Random Access Indexing

  Just like an ordinary primitive byte array, [`ByteBuf`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html) uses [zero-based indexing](https://en.wikipedia.org/wiki/Zero-based_numbering). It means the index of the first byte is always `0` and the index of the last byte is always [`capacity - 1`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#capacity--). For example, to iterate all bytes of a buffer, you can do the following, regardless of its internal implementation:

  ```
   ByteBuf buffer = ...;
   for (int i = 0; i < buffer.capacity(); i ++) {
       byte b = buffer.getByte(i);
       System.out.println((char) b);
   }
   
  ```

  ### Sequential Access Indexing

  [`ByteBuf`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html) provides two pointer variables to support sequential read and write operations - [`readerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readerIndex--) for a read operation and [`writerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#writerIndex--) for a write operation respectively. The following diagram shows how a buffer is segmented into three areas by the two pointers:

  ```
        +-------------------+------------------+------------------+
        | discardable bytes |  readable bytes  |  writable bytes  |
        |                   |     (CONTENT)    |                  |
        +-------------------+------------------+------------------+
        |                   |                  |                  |
        0      <=      readerIndex   <=   writerIndex    <=    capacity
   
  ```

  #### Readable bytes (the actual content)

  This segment is where the actual data is stored. Any operation whose name starts with `read` or `skip` will get or skip the data at the current [`readerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readerIndex--) and increase it by the number of read bytes. If the argument of the read operation is also a [`ByteBuf`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html) and no destination index is specified, the specified buffer's [`writerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#writerIndex--) is increased together.

  If there's not enough content left, [`IndexOutOfBoundsException`](https://docs.oracle.com/javase/7/docs/api/java/lang/IndexOutOfBoundsException.html?is-external=true) is raised. The default value of newly allocated, wrapped or copied buffer's [`readerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readerIndex--) is `0`.

  ```
   // Iterates the readable bytes of a buffer.
   ByteBuf buffer = ...;
   while (buffer.isReadable()) {
       System.out.println(buffer.readByte());
   }
   
  ```

  #### Writable bytes

  This segment is a undefined space which needs to be filled. Any operation whose name starts with `write` will write the data at the current [`writerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#writerIndex--) and increase it by the number of written bytes. If the argument of the write operation is also a [`ByteBuf`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html), and no source index is specified, the specified buffer's [`readerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readerIndex--) is increased together.

  If there's not enough writable bytes left, [`IndexOutOfBoundsException`](https://docs.oracle.com/javase/7/docs/api/java/lang/IndexOutOfBoundsException.html?is-external=true) is raised. The default value of newly allocated buffer's [`writerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#writerIndex--) is `0`. The default value of wrapped or copied buffer's [`writerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#writerIndex--) is the [`capacity`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#capacity--) of the buffer.

  ```
   // Fills the writable bytes of a buffer with random integers.
   ByteBuf buffer = ...;
   while (buffer.maxWritableBytes() >= 4) {
       buffer.writeInt(random.nextInt());
   }
   
  ```

  #### Discardable bytes

  This segment contains the bytes which were read already by a read operation. Initially, the size of this segment is `0`, but its size increases up to the [`writerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#writerIndex--) as read operations are executed. The read bytes can be discarded by calling [`discardReadBytes()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#discardReadBytes--) to reclaim unused area as depicted by the following diagram:

  ```
    BEFORE discardReadBytes()
  
        +-------------------+------------------+------------------+
        | discardable bytes |  readable bytes  |  writable bytes  |
        +-------------------+------------------+------------------+
        |                   |                  |                  |
        0      <=      readerIndex   <=   writerIndex    <=    capacity
  
  
    AFTER discardReadBytes()
  
        +------------------+--------------------------------------+
        |  readable bytes  |    writable bytes (got more space)   |
        +------------------+--------------------------------------+
        |                  |                                      |
   readerIndex (0) <= writerIndex (decreased)        <=        capacity
   
  ```

  Please note that there is no guarantee about the content of writable bytes after calling [`discardReadBytes()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#discardReadBytes--). The writable bytes will not be moved in most cases and could even be filled with completely different data depending on the underlying buffer implementation.

  #### Clearing the buffer indexes

  You can set both [`readerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readerIndex--) and [`writerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#writerIndex--) to `0` by calling [`clear()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#clear--). It does not clear the buffer content (e.g. filling with `0`) but just clears the two pointers. Please also note that the semantic of this operation is different from [`Buffer.clear()`](https://docs.oracle.com/javase/7/docs/api/java/nio/Buffer.html?is-external=true#clear--).

  ```
    BEFORE clear()
  
        +-------------------+------------------+------------------+
        | discardable bytes |  readable bytes  |  writable bytes  |
        +-------------------+------------------+------------------+
        |                   |                  |                  |
        0      <=      readerIndex   <=   writerIndex    <=    capacity
  
  
    AFTER clear()
  
        +---------------------------------------------------------+
        |             writable bytes (got more space)             |
        +---------------------------------------------------------+
        |                                                         |
        0 = readerIndex = writerIndex            <=            capacity
   
  ```

  ### Search operations

  For simple single-byte searches, use [`indexOf(int, int, byte)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#indexOf-int-int-byte-) and [`bytesBefore(int, int, byte)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#bytesBefore-int-int-byte-). [`bytesBefore(byte)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#bytesBefore-byte-) is especially useful when you deal with a `NUL`-terminated string. For complicated searches, use [`forEachByte(int, int, ByteProcessor)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#forEachByte-int-int-io.netty.util.ByteProcessor-) with a [`ByteProcessor`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/util/ByteProcessor.html) implementation.

  ### Mark and reset

  There are two marker indexes in every buffer. One is for storing [`readerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readerIndex--) and the other is for storing [`writerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#writerIndex--). You can always reposition one of the two indexes by calling a reset method. It works in a similar fashion to the mark and reset methods in [`InputStream`](https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html?is-external=true) except that there's no `readlimit`.

  ### Derived buffers

  You can create a view of an existing buffer by calling one of the following methods:

  - [`duplicate()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#duplicate--)
  - [`slice()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#slice--)
  - [`slice(int, int)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#slice-int-int-)
  - [`readSlice(int)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readSlice-int-)
  - [`retainedDuplicate()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#retainedDuplicate--)
  - [`retainedSlice()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#retainedSlice--)
  - [`retainedSlice(int, int)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#retainedSlice-int-int-)
  - [`readRetainedSlice(int)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readRetainedSlice-int-)

  A derived buffer will have an independent [`readerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readerIndex--), [`writerIndex`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#writerIndex--) and marker indexes, while it shares other internal data representation, just like a NIO buffer does.

  In case a completely fresh copy of an existing buffer is required, please call [`copy()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#copy--) method instead.

  #### Non-retained and retained derived buffers

  Note that the [`duplicate()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#duplicate--), [`slice()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#slice--), [`slice(int, int)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#slice-int-int-) and [`readSlice(int)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readSlice-int-) does NOT call [`retain()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#retain--) on the returned derived buffer, and thus its reference count will NOT be increased. If you need to create a derived buffer with increased reference count, consider using [`retainedDuplicate()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#retainedDuplicate--), [`retainedSlice()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#retainedSlice--), [`retainedSlice(int, int)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#retainedSlice-int-int-) and [`readRetainedSlice(int)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#readRetainedSlice-int-) which may return a buffer implementation that produces less garbage.

  ### Conversion to existing JDK types

  #### Byte array

  If a [`ByteBuf`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html) is backed by a byte array (i.e. `byte[]`), you can access it directly via the [`array()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#array--) method. To determine if a buffer is backed by a byte array, [`hasArray()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#hasArray--) should be used.

  #### NIO Buffers

  If a [`ByteBuf`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html) can be converted into an NIO [`ByteBuffer`](https://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html?is-external=true) which shares its content (i.e. view buffer), you can get it via the [`nioBuffer()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#nioBuffer--) method. To determine if a buffer can be converted into an NIO buffer, use [`nioBufferCount()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#nioBufferCount--).

  #### Strings

  Various [`toString(Charset)`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#toString-java.nio.charset.Charset-) methods convert a [`ByteBuf`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html) into a [`String`](https://docs.oracle.com/javase/7/docs/api/java/lang/String.html?is-external=true). Please note that [`toString()`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBuf.html#toString--) is not a conversion method.

  #### I/O Streams

  Please refer to [`ByteBufInputStream`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBufInputStream.html) and [`ByteBufOutputStream`](dfile:///Users/alton/Library/Application Support/Dash/Java DocSets/ionettynettyall/NettyAllinOne.docset/Contents/Resources/Documents/dash_javadoc/io/netty/buffer/ByteBufOutputStream.html).

