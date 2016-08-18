//
//  TextUnit.h
//  Stemmer
//
//  Created by TJT on 8/12/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RangeArray : NSObject

- (NSInteger)startAtIndex:(NSInteger)index;
- (NSInteger)endAtIndex:(NSInteger)index;
- (void)addWithStart:(NSInteger)start end:(NSInteger)end;
- (void)pop;
- (void)clear;
- (NSInteger)count;

@end


@interface TextUtil : NSObject

+ (NSString *)trim:(NSString *)str trimStart:(BOOL)trimStart trimEnd:(BOOL)trimEnd;

+ (BOOL)isLtter:(unichar)c;

// return NSNotFound if not found
+ (NSInteger)standaloneIndexOf:(NSString *)str key:(NSString *)key start:(NSInteger)start;

// return array of ranges{start, end}, // not {location, length}
+ (RangeArray *)split:(NSString *)str separator:(NSString *)separator;

+ (RangeArray *)split:(NSString *)str separator:(NSString *)separator outValue:(RangeArray *)outValue;

+ (unichar)simpleToLower:(unichar)c;
@end