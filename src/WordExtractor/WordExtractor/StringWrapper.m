//
//  StringWrapper.m
//  WordExtractor
//
//  Created by TJT on 8/18/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import "StringWrapper.h"
#import "TextUtil.h"

@interface StringWrapper ()

@property (nonatomic, assign) NSUInteger        hashCache;
@property (nonatomic, strong) NSString          *source;

@end

@implementation StringWrapper

+ (instancetype)stringWithString:(NSString *)string {
    StringWrapper *s = [[StringWrapper alloc] initWithString:string];
    return s;
}

- (instancetype)initWithString:(NSString *)aString {
    self = [super init];
    if (self) {
        _hashCache = 0;
        _source = aString;
    }
    return self;
}

- (unichar)characterAtIndex:(NSUInteger)index {
    return [_source characterAtIndex:index];
}

- (NSUInteger)length {
    return _source.length;
}

- (BOOL)isEqual:(id)other {
    if (other == self) {
        return YES;
    } else if ([other isKindOfClass:[NSString class]]) {
        NSString *str = (NSString *) other;
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

- (NSUInteger)hash {
    if (_hashCache == 0 && self.length > 0) {
        for (NSInteger i = 0; i < self.length; i++) {
            _hashCache = _hashCache * 31 + [TextUtil simpleToLower:[self characterAtIndex:i]];
        }
        return _hashCache;
    }
    return _hashCache;
}

- (id)copyWithZone:(NSZone *)zone {
    StringWrapper *copy = [[[self class] alloc] init];
    if (copy) {
        copy.source = self.source;
    }
    
    return copy;
}

- (NSString *)description {
    return _source;
}

@end