//
//  TextUnit.m
//  Stemmer
//
//  Created by TJT on 8/12/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import "TextUtil.h"

@implementation TextUtil

+ (BOOL)isLtter:(unichar)c {
    return (c > 47 && c < 58) || (c > 64 && c < 91) || (c > 96 && c < 123 ) || c == '-';
}

+ (unichar)simpleToLower:(unichar)c {
    if (c > 64 && c <= 90) {
        return c + 32;
    }
    return c;
}

+ (NSString *)trim:(NSString *)str trimStart:(BOOL)trimStart trimEnd:(BOOL)trimEnd {
    if (!(trimStart || trimEnd) || str.length == 0) {
        return str;
    }
    NSCharacterSet *cset = [NSCharacterSet characterSetWithCharactersInString:@" ?"];
    BOOL needTrimStart = NO;
    if (trimStart) {
        needTrimStart = [cset characterIsMember:[str characterAtIndex:0]];
    }
    
    BOOL needTrimEnd = NO;
    if (trimEnd) {
        needTrimEnd = [cset characterIsMember:[str characterAtIndex:str.length - 1]];
    }
    
    if (needTrimEnd || needTrimStart) {
        NSMutableString *mutableStr = [NSMutableString stringWithString:str];
        if (needTrimStart) {
            while ([cset characterIsMember:[mutableStr characterAtIndex:0]]) {
                [mutableStr deleteCharactersInRange:NSMakeRange(0, 1)];
            }
        }
        
        if (needTrimEnd) {
            while ([cset characterIsMember:[mutableStr characterAtIndex:mutableStr.length - 1]]) {
                [mutableStr deleteCharactersInRange:NSMakeRange(mutableStr.length - 1, 1)];
            }
        }
        return [mutableStr copy];
    }
    return str;
}

+ (NSInteger)standaloneIndexOf:(NSString *)str key:(NSString *)key start:(NSInteger)start {
    if (key.length == 0 || str.length == 0 || start >= str.length) {
        return NSNotFound;
    }
    NSInteger keylen = key.length;
    NSInteger strlen = str.length;
    if (keylen == strlen) {
        return [key isEqualToString:str] ? 0 : NSNotFound;
    }
    NSInteger pos = start;
    while (pos + keylen <= strlen) {
        pos = [str rangeOfString:key options:0 range:NSMakeRange(pos, str.length - pos)].location;
        if (pos == NSNotFound) {
            return NSNotFound;
        }
        if (pos == 0) {
            if ((pos + keylen < strlen) && ![self isLtter:[str characterAtIndex:pos + keylen]]) {
                return pos;
            } else {
                pos += keylen;
            }
        } else {
            if (![self isLtter:[str characterAtIndex:pos - 1]] && (pos + keylen >= strlen || ![self isLtter:[str characterAtIndex:pos + keylen]]) ) {
                return pos;
            } else {
                pos += keylen;
            }
        }
    }
    return NSNotFound;
}

+ (RangeArray *)split:(NSString *)str separator:(NSString *)separator {
    return [self split:str separator:separator outValue:[RangeArray new]];
}

+ (RangeArray *)split:(NSString *)str separator:(NSString *)separator outValue:(RangeArray *)outValue {
    [outValue clear];
    RangeArray *array = outValue;
    if (str.length == 0) {
        return array;
    }
    NSInteger start = 0;
    NSInteger end;
    while (YES) {
        end = [str rangeOfString:separator options:0 range:NSMakeRange(start, str.length - start)].location;
        if (end == NSNotFound) {
            if (start == 0) {
                return array;
            }
            [array addWithStart:start end:str.length];
            break;
        }
        [array addWithStart:start end:end];
        start = end + 1;
    }
    return array;
}

@end

@interface RangeArray ()

@property (nonatomic, strong) NSMutableArray        *array;

@end

@implementation RangeArray

- (instancetype)init
{
    self = [super init];
    if (self) {
        _array = [NSMutableArray new];
    }
    return self;
}

- (void)addWithStart:(NSInteger)start end:(NSInteger)end {
    [_array addObject:[NSNumber numberWithLong:start]];
    [_array addObject:[NSNumber numberWithLong:end]];
}

- (NSInteger)count {
    return _array.count / 2;
}

- (void)pop {
    if (_array.count == 0) {
        return;
    }
    [_array removeLastObject];
    [_array removeLastObject];
}

- (void)clear {
    [_array removeAllObjects];
}

- (NSInteger)startAtIndex:(NSInteger)index {
    if (index >= [self count]) {
        [NSException raise:@"index out of range" format:@""];
        return NSNotFound;
    }
    return [[_array objectAtIndex:index * 2] integerValue];
}

- (NSInteger)endAtIndex:(NSInteger)index {
    if (index >= [self count]) {
        [NSException raise:@"index out of range" format:@""];
        return NSNotFound;
    }
    return [[_array objectAtIndex:index * 2 + 1] integerValue];
}

@end
