//
//  SubString.m
//  WordExtractor
//
//  Created by TJT on 8/18/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import "SubString.h"
#import "TextUtil.h"

@interface SubString ()

@property (nonatomic, assign) NSUInteger        hashCache;
@property (nonatomic, strong) NSString          *source;

@end

@implementation SubString

- (instancetype)init {
    self = [super init];
    if (self) {
        _hashCache = 0;
    }
    return self;
}

- (void)updateWithString:(NSString *)str start:(NSInteger)start end:(NSInteger)end {
    if (start < 0 || end < 0) {
        [NSException raise:@"IndexOutOfBound" format:@"start(%lu) or end(%lu) < 0", start, end];
    }
    if (start > end) {
        [NSException raise:@"IndexOutOfBound" format:@"start(%lu) > end(%lu)", start, end];
    }
    if (end - start > str.length) {
        [NSException raise:@"IndexOutOfBound" format:@"length(end - start) > str.length"];
    }
    _source = str;
    _start = start;
    _end = end;
    _hashCache = 0;
}

- (NSUInteger)hash {
    if (_hashCache == 0 && self.length > 0) {
        for (NSInteger i = 0; i < self.length; i++) {
            _hashCache = _hashCache * 31 + [TextUtil simpleToLower:[self characterAtIndex:i]];
        }
        return _hashCache;
    }
    return _hashCache;
}

- (NSUInteger)length {
    return _end - _start;
}

- (unichar)characterAtIndex:(NSUInteger)index {
    return [_source characterAtIndex:_start + index];
}

- (BOOL)hasPrefix:(NSString *)str {
    if (str.length > self.length) {
        return NO;
    }
    for (NSInteger i = 0; i < str.length; i++) {
        if ([str characterAtIndex:i] != [self characterAtIndex:i]) {
            return NO;
        }
    }
    return true;
}

- (BOOL) hasSuffix:(NSString *)str {
    if (str.length > self.length) {
        return NO;
    }
    for (NSInteger i = 1; i <= str.length; i++) {
        if ([str characterAtIndex:str.length - i] != [self characterAtIndex:self.length - i]) {
            return NO;
        }
    }
    return YES;
}

- (NSString *)substringFromIndex:(NSUInteger)from {
    return [_source substringWithRange:NSMakeRange(_start + from, self.length - from)];
}

- (NSString *)substringToIndex:(NSUInteger)to {
    return [_source substringWithRange:NSMakeRange(_start, to)];
}

- (NSString *)substringWithRange:(NSRange)range {
    return [_source substringWithRange:NSMakeRange(range.location + _start, range.length)];
}


- (BOOL)isEqual:(id)object {
    if (object == self) {
        return YES;
    } else if ([object isKindOfClass:[NSString class]]) {
        NSString *str = (NSString *) object;
        if (str.length != self.length) {
            return NO;
        }
        for (NSUInteger i = 0; i < self.length; i++) {
            if ([TextUtil simpleToLower:[str characterAtIndex:i]] != [TextUtil simpleToLower:[self characterAtIndex:i]]) {
                return NO;
            }
        }
        return YES;
    }
    return NO;
}

- (id)copyWithZone:(NSZone *)zone {
    SubString *copy = [[[self class] alloc] init];
    if (copy) {
        [copy updateWithString:_source start:_start end:_end];
        copy.hashCache = _hashCache;
    }
    return copy;
}

- (NSString *)description {
    return [_source substringWithRange:NSMakeRange(_start, _end - _start)];
}

@end
